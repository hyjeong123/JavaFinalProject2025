package Slither;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements KeyListener, MouseMotionListener {

    private MyFrame frame;
    
    private PlayerWorm player;
    private final ArrayList<BotWorm> bots = new ArrayList<>();
    private final ArrayList<Food> food = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    
    private int bulletCount; 
    private int score = 0;   
    private final int MAX_FOOD_COUNT = 50;
    private final int BODIES_TO_FOOD_RATIO = 2;
    
    private int mouseX = 400;
    private int mouseY = 300;
    
    private Timer timer;

    public GamePanel(MyFrame frame) {
        this.frame = frame;

        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        
        int playerSize = frame.getPlayerSize();
        int quantity = frame.getWormQuantity();
        bulletCount = frame.getBulletQuantity(); 
        
        final int PLAYER_START_X = 400; 
        final int PLAYER_START_Y = 300; 
        final int MIN_SAFE_DISTANCE = 200; 
        
        player = new PlayerWorm(PLAYER_START_X, PLAYER_START_Y, playerSize, 5);
        
        // 봇 초기화 및 플레이어 주변 회피 로직 (이전과 동일)
        for (int i = 0; i < quantity; i++) {
            int botX, botY;
            boolean tooClose;
            
            do {
                botX = (int)(Math.random() * getWidth());
                botY = (int)(Math.random() * getHeight());
                
                double dx = botX - PLAYER_START_X;
                double dy = botY - PLAYER_START_Y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                
                tooClose = dist < MIN_SAFE_DISTANCE;
                
            } while (tooClose);
            
            int botsize = (int)(Math.random() * 80) + 20;
            bots.add(new BotWorm(botX, botY, botsize, 5));
        }
        
        // 초기 먹이 생성
        for (int i = 0; i < MAX_FOOD_COUNT; i++) {
            spawnFood();
        }

        // 🚨 [핵심 수정] 게임 시작 전 플레이어 강제 이동
        // 1. 초기 마우스 위치를 플레이어에서 살짝 떨어진 곳으로 설정 (이동 보장)
        mouseX = PLAYER_START_X + 100;
        mouseY = PLAYER_START_Y;
        
        // 2. move()를 충분히 호출하여 몸통 세그먼트들을 분리
        for(int i = 0; i < playerSize + 5; i++) {
            player.move(mouseX, mouseY);
        }

        // 타이머 시작 (이전과 동일)
        timer = new Timer(16, e -> { 
            updateGame();
            repaint();
        });

        timer.start();
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }
    
    private void spawnFood() {
        if (food.size() < MAX_FOOD_COUNT) {
            int foodX = (int)(Math.random() * getWidth());
            int foodY = (int)(Math.random() * getHeight());
            food.add(new Food(foodX, foodY));
        }
    }

    private void killWorm(Worm deadWorm) {
        List<?> body = deadWorm.getBody();
        for (int i = 0; i < body.size(); i += BODIES_TO_FOOD_RATIO) {
            Worm.Circle c = (Worm.Circle) body.get(i);
            food.add(new Food(c.getIntX(), c.getIntY()));
        }
        
        if (deadWorm instanceof BotWorm) {
            bots.remove(deadWorm);
        }
    }

    private void updateGame() {
        // 1. 플레이어 이동
        // 🚨 [주의] 마우스 위치는 mouseMoved 이벤트에서 실시간으로 업데이트되므로,
        // (400, 300)에 멈춰있지 않다는 전제하에 계속 움직입니다.
        player.move(mouseX, mouseY);
        
        // 2. 봇 이동
        for (BotWorm bot : bots) {
            bot.move((int)player.getX(), (int)player.getY());
        }
        
        // 3. 총알 이동 및 충돌 체크
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet b = bulletIterator.next();
            b.move();

            BotWorm hitBot = Collision.checkHitBotWithBullet(b, bots);
            if (hitBot != null) {
                b.setDead(); 
                hitBot.decrease(); 
                if (hitBot.getBody().size() <= 1) {
                    killWorm(hitBot);
                }
            }

            if (b.isOutsideBounds(getWidth(), getHeight()) || !b.isAlive()) {
                bulletIterator.remove();
            }
        }
        
        // 4. 먹이 생성
        if (Math.random() < 0.05 && food.size() < MAX_FOOD_COUNT) {
            spawnFood();
        }

        // 5. 충돌 체크 및 게임 오버
        boolean isGameOver = false;

        Food eaten = Collision.checkEatFood(player, food);
        if (eaten != null) {
            player.increase();
            score += 1;
            food.remove(eaten);
        }
        
        if (Collision.hitWall(player, getWidth(), getHeight()) || Collision.hitSelf(player)) {
            isGameOver = true;
        } else if (Collision.hitBots(player, bots)) {
            if (!frame.getHasShield()) {
                isGameOver = true;
            }
        }

        if (isGameOver) {
            timer.stop();
            frame.gameOver(score);
        }
    }
    
    // ----------------------------------------------------
    // 입력 이벤트 처리 (이전과 동일)
    // ----------------------------------------------------

    @Override public void keyTyped(KeyEvent e) { }
    
    @Override public void keyReleased(KeyEvent e) { 
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setSpeed(5); 
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (bulletCount > 0) {
                bullets.add(new Bullet(player.getX(), player.getY(), mouseX, mouseY));
                bulletCount--;
            }
        }
        
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setSpeed(7);
        }
    }

    @Override public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // ----------------------------------------------------
    // 렌더링 (이전과 동일)
    // ----------------------------------------------------

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경색
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        
        // 먹이
        g.setColor(Color.WHITE);
        for (Food f : food) {
            g.fillOval(f.getX() - f.getSize(), f.getY() - f.getSize(), f.getSize() * 2, f.getSize() * 2);
        }

        // 봇 지렁이들 그리기
        for (BotWorm b : bots) {
            for (Worm.Circle c : (List<Worm.Circle>) b.getBody()) {
                g.setColor(Color.YELLOW);
                g.fillOval(c.getIntX() - 6, c.getIntY() - 6, 12, 12);
            }

            Worm.Circle bh = b.getHead();
            if (bh != null) {
                g.setColor(Color.WHITE);
                g.drawOval(bh.getIntX() - 7, bh.getIntY() - 7, 14, 14);
            }
        }
        
        // 플레이어 몸
        for (Worm.Circle c : (List<Worm.Circle>) player.getBody()) {
            g.setColor(Color.GREEN);
            g.fillOval(c.getIntX() - 6, c.getIntY() - 6, 12, 12);
        }
        
        // 머리 강조 및 쉴드 표시
        Worm.Circle head = player.getHead();
        if (head != null) {
            g.setColor(Color.WHITE);
            g.drawOval(head.getIntX() - 7, head.getIntY() - 7, 14, 14);
            
            if (frame.getHasShield()) {
                g.setColor(new Color(0, 100, 255, 100)); 
                g.fillOval(head.getIntX() - 10, head.getIntY() - 10, 20, 20);
            }
        }

        // 총알
        g.setColor(Color.RED);
        for (Bullet b : bullets) {
            g.fillOval(b.getIntX() - 3, b.getIntY() - 3, 6, 6);
        }
        
        // HUD
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Bullets: " + bulletCount, 10, 40);
        String abilityStatus = frame.getHasShield() ? "Shield (Active)" : "Bullet";
        g.drawString("Ability: " + abilityStatus, 10, 60);
    }
}