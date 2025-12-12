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
    // 💡 화산 폭발 간격 랜덤화 상수
    private final int MIN_EXPLOSION_INTERVAL = 60 * 5; // 최소 5초 (300프레임)
    private final int MAX_EXPLOSION_INTERVAL = 60 * 15; // 최대 15초 (900프레임)
    private int volcanoTimer;
    
    // ====== [능력 관련 필드] ======
    private final int SHIELD_DURATION_FRAMES = 180; // 3초
    private int shieldTimer = 0;
    private boolean isShieldAbilitySelected = false;
    private boolean shieldUsed = false; // 💡 쉴드 1회 사용 여부
    private int bulletCount;    
    
    // ====== [게임 및 충돌 관련 필드] ======
    private int score = 0;    
    private final int MAX_FOOD_COUNT = 50;
    private final int BODIES_TO_FOOD_RATIO = 2;
    
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
        
        // 💡 화산 타이머 초기화: 랜덤 값 설정
        volcanoTimer = (int) (Math.random() * (MAX_EXPLOSION_INTERVAL - MIN_EXPLOSION_INTERVAL)) + MIN_EXPLOSION_INTERVAL;
        
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
            
            // 🐛 수정 1: 봇 생성 크기를 5 ~ 8로 변경하여 즉시 사망 버그 방지
            int botsize = (int)(Math.random() * 4) + 5;
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
        // 웜의 몸통을 먹이로 변환
        for (int i = 0; i < body.size(); i += BODIES_TO_FOOD_RATIO) {
            Worm.Circle c = (Worm.Circle) body.get(i);
            food.add(new Food(c.getIntX(), c.getIntY()));    
        }
        
        if (deadWorm instanceof BotWorm) {
            bots.remove(deadWorm);
        }
        // 플레이어는 여기서 제거하지 않음 (게임 오버 로직이 처리)
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
        
        // 🎯 수정 3: 플레이어의 머리가 없거나 길이가 0이면 게임 오버
        if (head == null || player.getSize() <= 0) {    
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

        
        // 3. 봇 이동
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
            // 💡 다음 타이머를 랜덤 값으로 재설정
            volcanoTimer = (int) (Math.random() * (MAX_EXPLOSION_INTERVAL - MIN_EXPLOSION_INTERVAL)) + MIN_EXPLOSION_INTERVAL;
        }

        // 6. 화산탄 이동 및 충돌 체크
        Iterator<VolcanoBullet> vbIterator = volcanoBullets.iterator();
        while (vbIterator.hasNext()) {
            VolcanoBullet vb = vbIterator.next();
            vb.move();

            Worm hitWorm = Collision.checkHitWormWithVolcanoBullet(vb, player, bots);    
            if (hitWorm != null) {    
                if (hitWorm == player && shieldTimer > 0) {
                     // 쉴드 활성화 시 무시
                } else {
                    hitWorm.decrease(5);    // 화산탄 맞으면 길이 5감소
                    vb.setDead();
                    
                    // 🎯 수정 4: 화산탄 피격 웜의 사망 조건 (길이가 0 이하일 때)
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
        
        // 7. 총알 이동 및 충돌 체크
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet b = bulletIterator.next();
            b.move();

            BotWorm hitBot = Collision.checkHitBotWithBullet(b, bots);
            if (hitBot != null) {
                hitBot.decrease(1);            // 총알 맞으면 길이 1감소    
                b.setDead();
                
                // 🎯 수정 2-1: 총알 맞은 봇의 제거 조건 (길이가 0 이하일 때)
                if (hitBot.getSize() <= 0) {
                    killWorm(hitBot);    
                }
            }

            if (b.isOutsideBounds(gameWidth, gameHeight) || !b.isAlive()) {
                bulletIterator.remove();
            }
        }
        
        // 8. 먹이 생성
        if (Math.random() < 0.05 && food.size() < MAX_FOOD_COUNT) {
            spawnFood(gameWidth, gameHeight);    
        }

        // 9. 충돌 체크 및 게임 오버
        
        if (shieldTimer <= 0) { // 쉴드 비활성화 시에만 충돌 체크
             // 벽 충돌 (길이가 0이 아닐 때만 사망)
             if (Collision.hitWall(player, gameWidth, gameHeight)) {
                 isGameOver = true;
             }
            
             // 봇 머리에 플레이어 머리 충돌 (길이가 0이 아닐 때만 사망)
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
            score += 2;            // 먹이 먹으면 길이 2증가
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
            // 🎯 수정 2-2: 봇 제거 조건 (길이가 0 이하일 때)
            if (bot.getSize() <= 0) {    
                botIterator.remove();
            }
        }

        // 💡 봇 제거 시 승리 조건
        if (bots.isEmpty() && !isGameOver) {
            timer.stop();
            frame.gameOver(score);    
            return;
        }

        // 최종 게임 오버 (플레이어 사망)
        if (isGameOver) {    
            timer.stop();
            frame.gameOver(score);
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
                // 💡 수정: shieldTimer가 0이고, 아직 사용되지 않았을 때만 활성화 (1회 제한)
                if (shieldTimer <= 0 && !shieldUsed) {    
                    shieldTimer = SHIELD_DURATION_FRAMES;
                    shieldUsed = true;    
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
             g.drawString("Ability (D: Shield): " + (shieldTimer > 0 ? (shieldTimer / 60 + 1) + "s" : (shieldUsed ? "Used" : "Ready")), 10, 60);
        }
    }
}