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
    
    // ====== [화산 관련 필드] ======
    private Volcano volcano;
    private final ArrayList<VolcanoBullet> volcanoBullets = new ArrayList<>();
    private final int VOLCANO_EXPLOSION_INTERVAL = 60 * 10; // 10초마다
    private int volcanoTimer = VOLCANO_EXPLOSION_INTERVAL; 
    
    // ... (나머지 기존 필드) ...
    private final int SHIELD_DURATION_FRAMES = 180; // 3초
    private int shieldTimer = 0;
    private boolean isShieldAbilitySelected = false;
    private int bulletCount; 
    
    private int score = 0;   
    private final int MAX_FOOD_COUNT = 50;
    private final int BODIES_TO_FOOD_RATIO = 2; // 죽었을 때 2 세그먼트 당 1개의 먹이
    
    private int mouseX = 400;
    private int mouseY = 300;
    
    private double currentAngle = 0; 
    private final int FORWARD_TARGET_DISTANCE = 300; 
    private boolean mouseMovedThisFrame = false; 
    
    private Timer timer;

    public GamePanel(MyFrame frame) {
        this.frame = frame;

        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        
        int playerSize = frame.getPlayerSize();
        int quantity = frame.getWormQuantity();
        bulletCount = frame.getBulletQuantity(); 
        
        // 능력 선택 로직
        if (frame.getBulletQuantity() == 0 && frame.getHasShield()) {
             isShieldAbilitySelected = true;
        } else {
             isShieldAbilitySelected = false; 
        }
        
        final int GAME_WIDTH = frame.getWidth();
        final int GAME_HEIGHT = frame.getHeight(); 
        
        final int PLAYER_START_X = GAME_WIDTH / 2; 
        final int PLAYER_START_Y = GAME_HEIGHT / 2; 
        final int MIN_SAFE_DISTANCE = 200; 
        
        // 플레이어 웜 초기화 (기본 속도 5)
        player = new PlayerWorm(PLAYER_START_X, PLAYER_START_Y, playerSize, 5);
        
        // 맵 중앙에 화산 초기화
        volcano = new Volcano(GAME_WIDTH / 2, GAME_HEIGHT / 2);
        
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
        
        // Food 초기화
        for (int i = 0; i < MAX_FOOD_COUNT; i++) {
            spawnFood(GAME_WIDTH, GAME_HEIGHT);
        }

        mouseX = PLAYER_START_X + 100;
        mouseY = PLAYER_START_Y;
        
        for(int i = 0; i < playerSize + 5; i++) {
            player.move(mouseX, mouseY);
        }
        
        currentAngle = Math.atan2(mouseY - PLAYER_START_Y, mouseX - PLAYER_START_X);


        timer = new Timer(16, e -> { 
            gamerunning();
            repaint();
        });

        timer.start();
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }
    
    public void stopGameLoop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    private void spawnFood(int w, int h) {
        if (food.size() < MAX_FOOD_COUNT) {
            int foodX = (int)(Math.random() * w);
            int foodY = (int)(Math.random() * h);
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
    
    private void explodeVolcano() {
        double x = volcano.getX();
        double y = volcano.getY();
        
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45); 
            volcanoBullets.add(new VolcanoBullet(x, y, angle));
        }
    }

    private void gamerunning() { 
        
        int gameWidth = getWidth();
        int gameHeight = getHeight();
        
        boolean isGameOver = false; 
        
        Worm.Circle head = player.getHead();
        if (head == null || player.getSize() <= 0) { // 뱀 길이가 0이되면 게임 오버
            isGameOver = true;
            if (isGameOver) {
                timer.stop();
                frame.gameOver(score);
            }
            return;
        }

        // 1. 플레이어 이동
        player.move(mouseX, mouseY);
        currentAngle = player.getCurrentAngle(); 
        
        // 2. 다음 프레임의 목표 설정
        if (mouseMovedThisFrame) {
            double dx = mouseX - head.getX();
            double dy = mouseY - head.getY();
            if (dx * dx + dy * dy > 1) {
                currentAngle = Math.atan2(dy, dx);
            }
        } else {
            mouseX = (int)(head.getX() + Math.cos(currentAngle) * FORWARD_TARGET_DISTANCE);
            mouseY = (int)(head.getY() + Math.sin(currentAngle) * FORWARD_TARGET_DISTANCE);
        }
        mouseMovedThisFrame = false;

        
        // 3. 봇 이동 (활성화)
        for (BotWorm bot : bots) {
            bot.move(player, food, bots, gameWidth, gameHeight); 
        }
        
        // 4. 쉴드 타이머 관리
        if (shieldTimer > 0) {
            shieldTimer--;
        }
        
        // 5. 화산 타이머 및 폭발 체크
        volcanoTimer--;
        if (volcanoTimer <= 0) {
            explodeVolcano();
            volcanoTimer = VOLCANO_EXPLOSION_INTERVAL;
        }

        // 6. 화산탄 이동 및 충돌 체크 (활성화)
        Iterator<VolcanoBullet> vbIterator = volcanoBullets.iterator();
        while (vbIterator.hasNext()) {
            VolcanoBullet vb = vbIterator.next();
            vb.move();

            Worm hitWorm = Collision.checkHitWormWithVolcanoBullet(vb, player, bots); 
            if (hitWorm != null) { 
                // 화산탄 피격 시 웜 길이를 10 감소시키고 화산탄 제거
                if (hitWorm == player && shieldTimer > 0) {
                     // 쉴드 활성화 시 무시
                } else {
                    hitWorm.decrease(10); 
                    vb.setDead();
                    if (hitWorm.getSize() <= 0) {
                        if (hitWorm == player) {
                            isGameOver = true;
                        } else {
                            killWorm(hitWorm);
                        }
                    }
                }
            }

            if (!vb.isAlive() || vb.getIntX() < -100 || vb.getIntX() > gameWidth + 100 || vb.getIntY() < -100 || vb.getIntY() > gameHeight + 100) {
                vbIterator.remove();
            }
        }
        
        // 7. 총알 이동 및 충돌 체크 (활성화)
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet b = bulletIterator.next();
            b.move();

            BotWorm hitBot = Collision.checkHitBotWithBullet(b, bots);
            if (hitBot != null) {
                // 총알 피격 시 봇 길이를 5 감소시키고 총알 제거
                hitBot.decrease(5); 
                b.setDead();
                
                // 봇이 너무 짧아지면 제거
                if (hitBot.getSize() < 5) {
                    killWorm(hitBot); 
                }
            }

            if (b.isOutsideBounds(gameWidth, gameHeight) || !b.isAlive()) {
                bulletIterator.remove();
            }
        }
        
        // 8. 먹이 생성 (활성화)
        if (Math.random() < 0.05 && food.size() < MAX_FOOD_COUNT) {
            spawnFood(gameWidth, gameHeight); 
        }

        // 9. 충돌 체크 및 게임 오버 (활성화)
        
        if (shieldTimer <= 0) { // 쉴드 비활성화 시에만 충돌 체크
             // 벽 충돌
             if (Collision.hitWall(player, gameWidth, gameHeight)) {
                 isGameOver = true;
             }
            
             // 봇 머리에 플레이어 머리 충돌
             if (Collision.hitBots(player, bots)) {
                 isGameOver = true;
             }
        }


        // 봇 충돌 (플레이어 몸통 충돌 및 봇 간 충돌)
        BotWorm hitBotByPlayer = Collision.checkBotHitPlayerBody(player, bots);
        if (hitBotByPlayer != null) {
            killWorm(hitBotByPlayer);
        }
        
        BotWorm hitBotByBot = Collision.checkBotHitBot(bots);
        if (hitBotByBot != null) {
            killWorm(hitBotByBot);
        }
        
        // 9-3. 먹이 먹기
        Food eatenFood = Collision.checkEatFood(player, food);
        if (eatenFood != null) {
            food.remove(eatenFood);
            player.increase(); 
            score += 10;
        }
        
        // 봇 먹이 먹기
        Iterator<BotWorm> botIterator = bots.iterator();
        while(botIterator.hasNext()) {
            BotWorm bot = botIterator.next();
            Food botEatenFood = Collision.checkEatFood(bot, food);
            if (botEatenFood != null) {
                food.remove(botEatenFood);
                bot.increase();
            }
            if (bot.getSize() < 5) { 
                botIterator.remove();
            }
        }

        // 최종 게임 오버
        if (isGameOver) { 
            timer.stop();
            frame.gameOver(score);
        }
    }
    
    // ... (mouseDragged, mouseMoved는 기존과 동일) ...

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        
        // 먹이 렌더링
        g.setColor(Color.WHITE);
        for (Food f : food) {
            g.fillOval(f.getX() - f.getSize(), f.getY() - f.getSize(), f.getSize() * 2, f.getSize() * 2);
        }
        
        // 화산 렌더링
        g.setColor(new Color(255, 69, 0)); 
        g.fillOval(volcano.getX() - volcano.getSize(), volcano.getY() - volcano.getSize(), volcano.getSize() * 2, volcano.getSize() * 2);
        
        // 화산탄 렌더링
        g.setColor(Color.RED);
        for (VolcanoBullet vb : volcanoBullets) {
            g.fillOval(vb.getIntX() - vb.getSize(), vb.getIntY() - vb.getSize(), vb.getSize() * 2, vb.getSize() * 2);
        }
        
        // 뱀 (봇과 플레이어) 렌더링
        
        // 봇 렌더링 (노란색)
        g.setColor(Color.YELLOW);
        for (BotWorm bot : bots) {
            if (bot.getBody() != null) {
                for (Worm.Circle c : (List<Worm.Circle>) bot.getBody()) {
                    g.fillOval(c.getIntX() - 6, c.getIntY() - 6, 12, 12);
                }
            }
        }

        // 플레이어 렌더링 (초록색)
        g.setColor(Color.GREEN);
        if (player.getHead() != null) {
            // 플레이어 몸통 렌더링
            for (Worm.Circle c : (List<Worm.Circle>) player.getBody()) {
                g.fillOval(c.getIntX() - 6, c.getIntY() - 6, 12, 12);
            }
            
            // 쉴드가 활성화된 경우 머리에 쉴드 테두리 추가
            if (shieldTimer > 0) {
                Worm.Circle head = player.getHead();
                g.setColor(new Color(0, 191, 255, 150)); // 투명한 하늘색 쉴드
                g.drawOval(head.getIntX() - 10, head.getIntY() - 10, 20, 20);
            }
        }

        // 총알 렌더링
        g.setColor(Color.CYAN);
        for (Bullet b : bullets) {
            g.fillOval(b.getIntX() - b.getSize(), b.getIntY() - b.getSize(), b.getSize() * 2, b.getSize() * 2);
        }


        // HUD
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Length: " + player.getSize(), 10, 40); 
        if (!isShieldAbilitySelected) {
             g.drawString("Ability (S: Fire): " + bulletCount + " left", 10, 60);
        } else {
             g.drawString("Ability (D: Shield): " + (shieldTimer > 0 ? (shieldTimer / 60 + 1) + "s" : "Ready"), 10, 60);
        }
    }

    @Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		mouseMovedThisFrame = true;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

    // 💡 최종 수정: 키 설정 문제 해결 (D, S, Space)
	@Override
	public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_SPACE) {
            // Space 키를 누르면 속도 증가
            player.setSpeed(7); 
            
        } else if (keyCode == KeyEvent.VK_D) {
            // D 키는 쉴드 능력 발동
            if (isShieldAbilitySelected) {
                if (shieldTimer <= 0) {
                    shieldTimer = SHIELD_DURATION_FRAMES;
                }
            }
            
        } else if (keyCode == KeyEvent.VK_S) {
            // S 키는 총알 발사
            if (!isShieldAbilitySelected) {
                if (bulletCount > 0) {
                    Worm.Circle head = player.getHead();
                    if (head != null) {
                        bullets.add(new Bullet(head.getX(), head.getY(), player.getCurrentAngle())); 
                        bulletCount--;
                    }
                }
            }
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Space 키를 떼면 속도 원래대로 복구
            player.setSpeed(5); // 기본 속도 5
        }
	}
}