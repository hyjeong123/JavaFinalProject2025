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

// JPanel 이랑 KeyEvent, MouseMotionEvent를 받는 GamePanel
public class GamePanel extends JPanel implements KeyListener, MouseMotionListener {
	//----------------- 전역 변수 영역--------------------------
    private MyFrame frame;	// MyFrame객체(MyFrame내의 함수 사용 위함
    
    private PlayerWorm player;	// 플레이어 객체
    private final ArrayList<BotWorm> bots = new ArrayList<>();	// bots, food, bullets 객체(여러 개 생성)
    private final ArrayList<Food> food = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    
    // 화산 폭발 관련 변수
    private Volcano volcano;	// 화산 객체(인스턴스 변수)
    private final ArrayList<VolcanoBullet> volcanoBullets = new ArrayList<>();	// 화산탄 객체
    
    private final int FRAME_RATE = 60; // 1000ms / 16ms ≈ 62.5, 약 60프레임으로 가정
    
    private int volcanoTimer;	// 한번 폭발하고 그 사이의 시간간격을 계산하는 타이머
    
    // 능력 관련 변수 모음
    private final int SHIELDTIME = 180; // 3초
    private int shieldTimer = 0;	// 방어막 타이머(0으로 초기화)
    private boolean shieldselected = false;	// 방어막 선택 여부 확인
    private boolean shieldused = false; // 방어막 사용 여부 확인
    private int bulletcount;	// 총알탄 남은 개수 확인    
    
    // 게임관리와 충돌 관련 변수
    private int score = 0;      // 점수 0으로 초기화	
    private final int FOODMAX = 50;	// 맵에 한번에 존재 가능한 먹이의 개수 50으로 설정
    
    private int mouseX = 400;	// 마우스 x좌표 시작위치(400)
    private int mouseY = 300;	// 마우스 y좌표 시작위치(300)
    
    private double wormangle = 0;    // 플레이어 지렁이의 각도 0으로 초기화
    private final int MOVINGDISTANCE = 300;    // 마우스가 움직이지 않을 때 지렁이가 바라보는 방향으로 설정할 목표 지점까지의 거리
    private boolean mousemoved = false;    // 현 프레임에서 마우스가 움직였는지 확인하는 변수
    
    private Timer timer;	// Timer 객체(게임 반복 루프 실행)
    
    // -----------생성자 영역--------------------------
    public GamePanel(MyFrame frame) {	// GamePanel 생성자 영역(파라미터 frame)
        this.frame = frame;

        setFocusable(true);				// GamePanel에서 키보드 입력을 받도록 함
        addKeyListener(this);			// KeyEvent추가
        addMouseMotionListener(this);	// MouseMotionEvent추가
        
        // 지역변수
        int playersize = frame.getPlayerSize();		// playersize와 quantity는 MyFrame내 get함수를 가져온다
        int quantity = frame.getWormQuantity();		// SettingPanel에서 설정한 값 들고오기 때문
        bulletcount = frame.getBulletQuantity();    
        
        // 능력 선택 로직
        if (frame.getBulletQuantity() == 0 && frame.getHasShield()) {
        	shieldselected = true;
        } else {
        	shieldselected = false;    
        }
        
        // 상수 설정영역
        final int GAMEWIDTH = frame.getWidth();
        final int GAMEHEIGHT = frame.getHeight();    
        
        final int PLAYER_START_X = GAMEWIDTH / 2;    
        final int PLAYER_START_Y = GAMEHEIGHT / 2;    
        final int SAFEDISTANCE = 200;    
        
        // 봇끼리 떨어져야 할 최소 거리
        final int BOT_SAFE_DISTANCE = 50;
        
        // PlayerWorm 초기화 (기본 속도 3)
        player = new PlayerWorm(PLAYER_START_X, PLAYER_START_Y, playersize, 3);
        
        // 화산 초기화(맵 중앙영역에 생성)
        volcano = new Volcano(GAMEWIDTH / 2, GAMEHEIGHT / 2);
        
        // 화산 타이머 초기화 로직
        int minFrames = 4 * FRAME_RATE; // 4 * 60 = 240
        int maxFrames = 6 * FRAME_RATE; // 6 * 60 = 360
        volcanoTimer = (int) (Math.random() * (maxFrames - minFrames + 1)) + minFrames;
        
        // 봇 초기화영역
        // bot의 개수(quantity만큼 순회함)
        for (int i = 0; i < quantity; i++) {
            int botx, boty;
            boolean tooClose;	// 너무 붙으면 서로 죽기 때문에 확인하는 변수
            
            // 지렁이를 생성하라
            do {
            	// 생성될 봇 지렁이들의 x, y좌표 무작위 생성
            	botx = (int)(Math.random() * GAMEWIDTH);    
            	boty = (int)(Math.random() * GAMEHEIGHT);	
                
                //  플레이어와의 거리 확인
                double dx_player = botx - PLAYER_START_X;
                double dy_player = boty - PLAYER_START_Y;
                double botplayerdis = (dx_player * dx_player) + (dy_player * dy_player);
                
                tooClose = botplayerdis < (SAFEDISTANCE * SAFEDISTANCE);
                
                // 기존 봇들과의 거리 확인**
                if (!tooClose) {
                    for (BotWorm panelbot : bots) {
                        double dx_bot = botx - panelbot.getHead().getX();
                        double dy_bot = boty - panelbot.getHead().getY();
                        double botbotdis = (dx_bot * dx_bot) + (dy_bot * dy_bot);

                        if (botbotdis < (BOT_SAFE_DISTANCE * BOT_SAFE_DISTANCE)) {
                            tooClose = true;
                            break;	
                        }
                    }
                }
                
            } while (tooClose);
            
            // 봇 생성 크기를 5 ~ 8로 설정(그 이상이면 생성하면서 지렁이의 머리와 몸통에 부딪혀 시작전부터 사망하는 불상사 방지)
            int botsize = (int)(Math.random() * 4) + 5;
            bots.add(new BotWorm(botx, boty, botsize, 3));	// BotWorm 추가(스피드 3)
        }
        
        // Food 초기화(50개 생성)
        for (int i = 0; i < FOODMAX; i++) {
            spawnFood(GAMEWIDTH, GAMEHEIGHT);
        }
        
        // 실제 마우스 위치를 조금 더 옮김(그래야 처음 마우스를 가만히 뒀을때 델타값이 있어서 방향 계산 가능)
        mouseX = PLAYER_START_X + 100;
        mouseY = PLAYER_START_Y;
        
        // 플레이어지렁이가 playerSize만큼 초기 몸통길이를 부여하고 이를 움직이게하기 위해 move호출을 계속한다
        for(int i = 0; i < playersize; i++) {
            player.move(mouseX, mouseY);
        }
        
        // 플레이어지렁이의 방향을 계산할 각도 구하기(아크 탄젠트 2)
        wormangle = Math.atan2(mouseY - PLAYER_START_Y, mouseX - PLAYER_START_X);

        // Timer은 16ms의 단위로 running한다 Timer내에는 게임 전체관리하는 함수랑 다시 그리는 함수 두가지가 존재
        // 1000ms / 16ms = 62.5 62.5FPS
        timer = new Timer(16, e -> {    
            gamerunning();
            repaint();
        });

        timer.start();
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());	// 이벤트 디스패리 스레드 내에서 실행하라고 넣는 코드
    }
 // ---------------- 생성자 종료 ---------------------------------
 // ---------------- 사용자 정의 메소드 영역-----------------------	
    
    // 게임종료 함수
    public void gameStop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
    
    // 먹이 생성함수(먹이개수가 50개보다 적으면 Frame에 랜덤하게 생성)
    private void spawnFood(int w, int h) {
        if (food.size() < FOODMAX) {
            int foodX = (int)(Math.random() * w);
            int foodY = (int)(Math.random() * h);
            food.add(new Food(foodX, foodY));    
        }
    }
    
    // 봇 지렁이 사망, 사망시 먹이 생성 함수(파라미터 Worm타입의 인스턴스)
    private void killWorm(Worm deadWorm) {
        List<?> body = deadWorm.getBody();	// 죽은 지렁이의 몸통을 가져와 body에 저장한다
        // 웜의 몸통을 먹이로 변환
        for (int i = 0; i < body.size(); i += 2) { // 💡 수정: 몸통 2개당 먹이 1개 생성 (i += 2)
            Worm.Circle c = (Worm.Circle) body.get(i);		// body의 클래스마다 Worm.Circle로 강제 형변환을 한다
            food.add(new Food(c.getIntX(), c.getIntY()));    // 죽은 지렁이 원의 좌표마다 food를 생성    
        }
        
        if (deadWorm instanceof BotWorm) {	 // 죽은 인스턴스 deadWorm이 BotWorm이라면 생성된 BotWorm객체에서 제거한다    
            bots.remove(deadWorm);
        }
        // 플레이어는 여기서 제거하지 않음 (게임 오버 로직이 처리)
    }
    
    // 화산 폭발 메소드
    private void explodeVolcano() {
    	// 화산의 x, y좌표를 가져옴(화산탄의 시작점)
        double x = volcano.getX();	
        double y = volcano.getY();	
        
        // 8방위로 각 총알을 1개씩 발사함
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);    // 8방위으 각도로 날아가게 각도를 설정한다(180도를 라디안 단위로 변환)
            volcanoBullets.add(new VolcanoBullet(x, y, angle));		// 각 index별 발사 8방위
        }
    }
    
    //------------ Timer내에서 게임이 진행되게 하는 메소드---------------------
    // 게임 진행 메소드영역 (16ms마다 호출됨)
    private void gamerunning() {    
        // GamePanel의 폭 높이를 가져옴
        int gameWidth = getWidth();
        int gameHeight = getHeight();
        
        boolean gameover = false;		// 게임오버가 되었는지 확인(false로 초기화)
        
        Worm.Circle head = player.getHead();	// player 객체의 머리를 들고옴
        
        // 플레이어의 머리가 없거나 길이가 0이면 게임 오버
        if (head == null || player.getSize() <= 0) {    
        	gameover = true;
            if (gameover) {
                timer.stop();
                if (player.getSize() > 0) {			// if 몸길이가 남아있다면 점수는 몸길이
                    score = player.getSize();
                }	
                else {							// 만약 죽어서 게임 끝나면 점수는 0점
                    score = 0;
                }
                frame.gameOver(score);		// FinPanel에 점수를 전달하기 위해 frame.의 함수호출
            }
            return;
        }

        // 플레이어 이동
        player.move(mouseX, mouseY);
        wormangle = player.getangle();    // Player의 각도를 조정한다
        
        // 다음 프레임의 목표 설정
        if (mousemoved) {
            double dx = mouseX - head.getX();	// 마우스와 player지렁이의 x, y의 변화량을 구해서
            double dy = mouseY - head.getY();	// 각도를 계산한다
            if (dx * dx + dy * dy > 1) {		// 움직임이 있다면 각도를 계산한다
            	wormangle = Math.atan2(dy, dx);
            }
        } else {	// 움직임이 없다면 기존 각도로 구해진 방향으로 300 이동하라
            mouseX = (int)(head.getX() + Math.cos(wormangle) * MOVINGDISTANCE);
            mouseY = (int)(head.getY() + Math.sin(wormangle) * MOVINGDISTANCE);
        }
        mousemoved = false;	// 머리 움직이지 않았다고 함

        
        // 봇 이동(초기화)
        for (BotWorm bot : bots) {
            bot.move(player, food, bots, gameWidth, gameHeight);    
        }
        
        // 쉴드 타이머 관리
        if (shieldTimer > 0) {
            shieldTimer--;	// 16ms마다 1씩 줄어듦
        }
        
        // 5. 화산 타이머 및 폭발 체크
        volcanoTimer--;	// 한번 터지면 1씩 줄어듦
        if (volcanoTimer <= 0) {
            explodeVolcano();
            // 💡 수정: 다음 타이머를 4~6초 랜덤 값으로 재설정 (하드코딩된 값 사용)
            int minFrames = 4 * FRAME_RATE; 
            int maxFrames = 6 * FRAME_RATE;
            volcanoTimer = (int) (Math.random() * (maxFrames - minFrames + 1)) + minFrames;
        }

        // 화산탄 이동 및 지렁이와의 충돌 체크(화산탄을 순차적으로 확인하기 위해 Iterator사용)
        Iterator<VolcanoBullet> vbIterator = volcanoBullets.iterator();
        while (vbIterator.hasNext()) {		// 화산탄이 있다면 true를 반환하고 넘어감
            VolcanoBullet vb = vbIterator.next();
            vb.move();		// 화산탄을 움직인다

            Worm hitWorm = Collision.checkHitWormWithVolcanoBullet(vb, player, bots);    // Worm이 플레이어 혹은 봇 지렁이와 부딪히는지 확인
            if (hitWorm != null) {    // 만약 지렁이가 부딪혔다면 진행할 조건문
                if (hitWorm == player && shieldTimer > 0) {		// 쉴드 활성화 되었을 때 무시
    
                } else {
                    hitWorm.decrease(5);    // 화산탄 맞으면 길이 5감소
                    vb.bulletDead();		// 화산탄 삭제(VolcanoBullet내의 함수 가져옴)
                    
                    // 화산탄 피격 웜의 사망 조건 (길이가 0 이하일 때)
                    if (hitWorm.getSize() <= 0) {
                        if (hitWorm == player) {
                        	gameover = true;	// 피격당했는데 player몸길이가 0되면 게임 끝
                        } else {
                            killWorm(hitWorm);	// 맞은 지렁이가 playerWorm이 아니라면 지렁이 없앰
                        }
                    }
                }
            }
            
            // 화산탄이 경계 밖으로 나가면 삭제함
            if (!vb.bulletAlive() || vb.getIntX() < -100 || vb.getIntX() > gameWidth + 100 || vb.getIntY() < -100 || vb.getIntY() > gameHeight + 100) {
                vbIterator.remove();
            }
        }
        
        // 총알 이동 및 충돌 체크
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet b = bulletIterator.next();
            b.move();

            BotWorm hitBot = Collision.checkHitBotWithBullet(b, bots);
            if (hitBot != null) {
                hitBot.decrease(1);    	// 총알 맞으면 길이 1감소    
                b.bulletDead();			// 총알 없애기
                
                // 총알 맞은 봇의 제거 조건 (길이가 0 이하일 때)
                if (hitBot.getSize() <= 0) {
                    killWorm(hitBot);    
                }
            }
            // 화면 밖으로 나가면 없애버림
            if (b.outofRange(gameWidth, gameHeight) || !b.bulletAlive()) {
                bulletIterator.remove();
            }
        }
        
        // 먹이 생성
        if (Math.random() < 0.05 && food.size() < FOODMAX) {
            spawnFood(gameWidth, gameHeight);    
        }

        // PlayerWorm 충돌 체크 및 게임 오버        
        if (shieldTimer <= 0) { // 쉴드 비활성화 시에만 충돌 체크
             // 벽 충돌 (길이가 0이 아닐 때만 사망)
             if (Collision.hitWall(player, gameWidth, gameHeight)) {
            	 gameover = true;
             }
            
             // 봇 몸통에 플레이어 머리 충돌 (길이가 0이 아닐 때만 사망)
             if (Collision.hitBots(player, bots)) {
            	 gameover = true;
             }
        }


        // 플레이어 몸통 vs 봇의 머리(피해자)
        BotWorm hitBotByPlayer = Collision.checkBotHitPlayerBody(player, bots);
        if (hitBotByPlayer != null) {
            killWorm(hitBotByPlayer);
        }
        
        // 봇의 몸통 vs 봇의 머리(피해자)
        BotWorm hitBotByBot = Collision.checkBotHitBot(bots);
        if (hitBotByBot != null) {
            killWorm(hitBotByBot);
        }
        
        // PlayerWorm의 먹이 먹기
        Food eatFood = Collision.checkEatFood(player, food);	// 플레이어와 먹이의 충돌
        if (eatFood != null) {		// 만약 먹었다면
            food.remove(eatFood);		// 먹은 음식을 없애고
            player.increase();    		// 플레이어 지렁이의 몸 길이 증가 
            score = player.getSize(); 	// 점수를 현재 플레이어 길이로 설정합니다.
        }
        
        // BotWorm의 먹이 먹기
        Iterator<BotWorm> botIterator = bots.iterator();
        while(botIterator.hasNext()) {		// 저장된 모든 지렁이를 순회
            BotWorm bot = botIterator.next();	
            Food botEatFood = Collision.checkEatFood(bot, food);	// 먹었는지 체크
            if (botEatFood != null) {		// 만약 먹었다면
                food.remove(botEatFood);	// 먹이 삭제
                bot.increase();				// 봇 지렁이 몸길이 증가
            }
            // 봇 사망
            if (bot.getSize() <= 0) {    
                botIterator.remove();	// 봇 인스턴스 제거
            }
        }

        // 모든 BotWorm 제거 시 승리 
        if (bots.isEmpty() && !gameover) {
            timer.stop();
            score = player.getSize(); // 최종 점수를 길이로 확정
            frame.gameOver(score);    
            return;
        }

        // 최종 게임 오버 (플레이어 사망)
        if (gameover) {    
            timer.stop();	// 타이머 정지
            if (player.getSize() > 0) {
                score = player.getSize();
            } else {
                score = 0;
            }
            frame.gameOver(score);
        }
    }
    //-------------- gamerunning() 종료------------------
// -------------사용자 정의 메소드 영역 종료-----------------------
// -------------이벤트 처리 영역 -------------------------------
    
    // MouseMotion영역
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// 커서 x, y좌표 가져오고 프레임 움직임을 true로 바꿈
		mouseX = e.getX();
		mouseY = e.getY();
		mousemoved = true;
	}
	
	// KeyEvent영역
	@Override
	public void keyTyped(KeyEvent e) { }

	
	@Override
	public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        // Spacebar 눌릴때 속도 증가한다
        if (keyCode == KeyEvent.VK_SPACE) {
            // Space 키를 누르면 속도 증가
            player.setSpeed(5);    
            
        }
        // D키 눌릴때 방어막 활성화
        else if (keyCode == KeyEvent.VK_D) {
            if (shieldselected) {
                // shieldTimer가 0이고, 아직 사용되지 않았을 때만 활성화 (1회 제한)
                if (shieldTimer <= 0 && !shieldused) {    
                    shieldTimer = SHIELDTIME;
                    shieldused = true;    
                }
            }
            
        }
        // S키 눌릴때 총알 발사
        else if (keyCode == KeyEvent.VK_S) {
            if (!shieldselected) {
                if (bulletcount > 0) {
                    Worm.Circle head = player.getHead();	// 머리가 향하는 방향으로 쏠거기 때문에 머리 가져옴
                    if (head != null) {
                        bullets.add(new Bullet(head.getX(), head.getY(), player.getangle()));    
                        bulletcount--;	// 한 발 쏘면 감소
                    }
                }
            }
        }
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// Space 키를 떼면 속도 원래대로 복구
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setSpeed(3); // 기본 속도 3
        }
	}
// ---------------------- 그리기 영역 ------------------------------    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 배경을 검게 칠한다
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        
        // 먹이는 하얀색으로 그린다
        g.setColor(Color.WHITE);
        for (Food f : food) {
            g.fillOval(f.getX() - f.getSize(), f.getY() - f.getSize(), f.getSize() * 2, f.getSize() * 2);
        }
        
        // 화산 렌더링 (삼각형으로 변경된 부분)
        g.setColor(new Color(255, 69, 0)); // 주홍색으로 칠함
        
        int centerX = volcano.getX();
        int centerY = volcano.getY();
        int size = volcano.getSize();
        
        int[] xPoints = {
            centerX,
            centerX - size,
            centerX + size
        };
        int[] yPoints = {
            centerY - size, 
            centerY + size,
            centerY + size
        };
        
        g.fillPolygon(xPoints, yPoints, 3);
        
        // 화산탄 그림(원형)
        g.setColor(Color.RED);
        for (VolcanoBullet vb : volcanoBullets) {
            g.fillOval(vb.getIntX() - vb.getSize(), vb.getIntY() - vb.getSize(), vb.getSize() * 2, vb.getSize() * 2);
        }
        
        // BotWorm 그림(노란색)
        g.setColor(Color.YELLOW);
        for (BotWorm bot : bots) {
            if (bot.getBody() != null) {
                for (Worm.Circle c : (List<Worm.Circle>) bot.getBody()) {
                    g.fillOval(c.getIntX() - 6, c.getIntY() - 6, 12, 12);
                }
            }
        }

        // PlayerWorm 그림(초록색)
        g.setColor(Color.GREEN);
        if (player.getHead() != null) {
            // 플레이어 몸통 렌더링
            for (Worm.Circle c : (List<Worm.Circle>) player.getBody()) {
                g.fillOval(c.getIntX() - 6, c.getIntY() - 6, 12, 12);
            }
            
            // 쉴드가 활성화된 경우 머리에 쉴드 테두리 추가
            if (shieldTimer > 0) {
                Worm.Circle head = player.getHead();
                g.setColor(new Color(0, 191, 255, 150)); // 반투명한 하늘색 쉴드
                g.drawOval(head.getIntX() - 10, head.getIntY() - 10, 20, 20);
            }
        }

        // 총알 그리기
        g.setColor(Color.CYAN);
        for (Bullet b : bullets) {
            g.fillOval(b.getIntX() - b.getSize(), b.getIntY() - b.getSize(), b.getSize() * 2, b.getSize() * 2);
        }


        // 흰색 안내판 글씨
        g.setColor(Color.WHITE);
        g.drawString("몸길이: " + player.getSize(), 10, 40);    
        
        String abilityleft;
        if (shieldselected) {
            if (shieldTimer > 0) {
            	abilityleft = (shieldTimer / 60 + 1) + "s";
            } else if (shieldused) {
            	abilityleft = "사용불가";
            } else {
            	abilityleft = "사용가능";
            }
            g.drawString("방어막(D키): " + abilityleft, 10, 60);	// (문자열, x, y) 위치
        } else {
             g.drawString("총알(S키): " + bulletcount + "발 남음", 10, 60);
        }
    }
}