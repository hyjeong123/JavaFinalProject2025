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
    
    // ====== [쉴드 기능 관련 필드] ======
    private final int SHIELD_DURATION_FRAMES = 180; // 3초 (60 FPS 기준)
    private int shieldTimer = 0; // 남은 쉴드 시간 (프레임 단위)
    private boolean isShieldAbilitySelected = false; // 설정에서 쉴드 능력을 선택했는지 여부
    private int bulletCount; 
    
    private int score = 0;   
    private final int MAX_FOOD_COUNT = 50;
    private final int BODIES_TO_FOOD_RATIO = 2; // 뱀 사망 시 먹이 생성 비율
    
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
        
        // 쉴드 능력 선택 확인
        if (frame.getBulletQuantity() == 0 && frame.getHasShield()) {
             isShieldAbilitySelected = true;
        }
        
        final int GAME_WIDTH = frame.getWidth();
        final int GAME_HEIGHT = frame.getHeight(); 
        
        final int PLAYER_START_X = GAME_WIDTH / 2; 
        final int PLAYER_START_Y = GAME_HEIGHT / 2; 
        final int MIN_SAFE_DISTANCE = 200; 
        
        player = new PlayerWorm(PLAYER_START_X, PLAYER_START_Y, playerSize, 5);
        
        // 봇 초기화
        for (int i = 0; i < quantity; i++) {
            int botX, botY;
            boolean tooClose;
            
            do {
                botX = (int)(Math.random() * GAME_WIDTH); 
                botY = (int)(Math.random() * GAME_HEIGHT);
                
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
            spawnFood(GAME_WIDTH, GAME_HEIGHT);
        }

        // 게임 시작 전 플레이어 강제 이동
        mouseX = PLAYER_START_X + 100;
        mouseY = PLAYER_START_Y;
        
        for(int i = 0; i < playerSize + 5; i++) {
            player.move(mouseX, mouseY);
        }

        timer = new Timer(16, e -> { 
            gamerunning();
            repaint();
        });

        timer.start();
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }
    
    // ====== [GamePanel 외부에서 타이머를 안전하게 종료하는 메서드] ======
    public void stopGameLoop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
    // =========================================================

    private void spawnFood(int w, int h) {
        if (food.size() < MAX_FOOD_COUNT) {
            int foodX = (int)(Math.random() * w);
            int foodY = (int)(Math.random() * h);
            food.add(new Food(foodX, foodY));
        }
    }

    // 뱀이 죽었을 때 먹이를 생성하고 리스트에서 제거
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

    private void gamerunning() { 
        
        int gameWidth = getWidth();
        int gameHeight = getHeight();
        
        // 1. 플레이어 이동
        player.move(mouseX, mouseY);
        
        // 2. 봇 이동 (AI 로직 호출)
        for (BotWorm bot : bots) {
            bot.move(player, gameWidth, gameHeight); 
        }
        
        // 3. 쉴드 타이머 관리
        if (shieldTimer > 0) {
            shieldTimer--;
        }
        
        // 4. 총알 이동 및 충돌 체크
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet b = bulletIterator.next();
            b.move();

            // 총알이 봇의 몸통(머리 제외)과 충돌했는지 체크
            BotWorm hitBot = Collision.checkHitBotWithBullet(b, bots);
            if (hitBot != null) {
                b.setDead(); 
                hitBot.decrease(); 
                if (hitBot.getBody().size() <= 1) {
                    killWorm(hitBot);
                }
            }

            if (b.isOutsideBounds(gameWidth, gameHeight) || !b.isAlive()) {
                bulletIterator.remove();
            }
        }
        
        // 5. 먹이 생성
        if (Math.random() < 0.05 && food.size() < MAX_FOOD_COUNT) {
            spawnFood(gameWidth, gameHeight); 
        }

        // 6. 충돌 체크 및 게임 오버
        boolean isGameOver = false;

        // 먹이 섭취
        Food eaten = Collision.checkEatFood(player, food);
        if (eaten != null) {
            player.increase();
            score += 1;
            food.remove(eaten);
        }
        
        // 🚨 봇의 머리가 플레이어의 몸통에 닿았을 경우 (봇 사망)
        BotWorm hitBotByPlayerBody = Collision.checkBotHitPlayerBody(player, bots);
        if (hitBotByPlayerBody != null) {
            killWorm(hitBotByPlayerBody); // 충돌한 봇 사망 및 먹이로 분해
        }

        // 플레이어 사망 조건
        if (Collision.hitWall(player, gameWidth, gameHeight)) {
            isGameOver = true;
        } else if (Collision.hitBots(player, bots)) { 
            // 봇의 몸통/머리에 플레이어 머리가 닿았는지 체크
            if (shieldTimer <= 0) { 
                isGameOver = true;
            }
        }

        if (isGameOver) {
            timer.stop();
            frame.gameOver(score);
        }
    }
    
    // ----------------------------------------------------
    // 입력 이벤트 처리
    // ----------------------------------------------------

    @Override public void keyTyped(KeyEvent e) { }
    
    @Override public void keyReleased(KeyEvent e) { 
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setSpeed(5); 
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setSpeed(7);
        }
        
        // D 키 입력 처리: 쉴드 또는 총알 발사
        if (e.getKeyCode() == KeyEvent.VK_D) { 
            if (isShieldAbilitySelected) {
                // 쉴드 능력 선택 시: 쉴드 타이머가 0일 때만 활성화 (쿨다운처럼 동작)
                if (shieldTimer <= 0) {
                     shieldTimer = SHIELD_DURATION_FRAMES;
                }
            } else {
                // 총알 능력 선택 시: 총알 발사
                if (bulletCount > 0) {
                    bullets.add(new Bullet(player.getX(), player.getY(), mouseX, mouseY));
                    bulletCount--;
                }
            }
        }
    }

    @Override public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // ----------------------------------------------------
    // 렌더링
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

        // 봇 지렁이들 그리기 (몸통: 노란색, 머리 테두리: 흰색)
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
        
        // 플레이어 몸 (녹색)
        for (Worm.Circle c : (List<Worm.Circle>) player.getBody()) {
            g.setColor(Color.GREEN);
            g.fillOval(c.getIntX() - 6, c.getIntY() - 6, 12, 12);
        }
        
        // 머리 강조 및 쉴드 표시
        Worm.Circle head = player.getHead();
        if (head != null) {
            g.setColor(Color.WHITE);
            g.drawOval(head.getIntX() - 7, head.getIntY() - 7, 14, 14);
            
            // 쉴드 타이머가 0보다 클 때 쉴드 시각 효과 표시
            if (shieldTimer > 0) {
                // 종료 임박 시 깜빡이는 효과 (마지막 1초)
                int alpha = (shieldTimer < 60 && shieldTimer % 10 < 5) ? 100 : 200; 
                
                g.setColor(new Color(0, 100, 255, alpha)); 
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
        
        // 능력 HUD 표시
        if (isShieldAbilitySelected) {
            // 쉴드 남은 시간 표시
            String shieldTimeStr = String.format("Shield (D): %.1fs", (float)shieldTimer / 60.0f);
            g.drawString(shieldTimeStr, 10, 40);
        } else {
             // 총알 개수 표시
             g.drawString("Bullets (D): " + bulletCount, 10, 40);
        }
    }
}