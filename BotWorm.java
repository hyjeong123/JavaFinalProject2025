package Slither;

// BotWorm은 Worm의 getHead()를 사용하기 위해 Worm.Circle에 접근해야 합니다.

public class BotWorm extends Worm {
    
    // 봇의 현재 목표 좌표
    private int currentTargetX;
    private int currentTargetY;
    private final int WORM_SEGMENT_RADIUS = 6; 
    
    public BotWorm(int x, int y, int size, int speed) {
        super(x, y, size, speed);
        // 초기 목표 설정
        this.currentTargetX = x + 100;
        this.currentTargetY = y;
    }
    
    // GamePanel에서 호출할 새로운 move 메서드
    public void move(PlayerWorm player, int gameWidth, int gameHeight) {
        
        // 1. 벽 회피 로직
        Worm.Circle head = getHead();
        if (head == null) return;
        
        int headX = head.getIntX();
        int headY = head.getIntY();
        int SAFE_MARGIN = 50 + WORM_SEGMENT_RADIUS; 

        boolean nearWall = false;
        
        // X축 경계 검사 및 목표 반대 방향 설정
        if (headX < SAFE_MARGIN) { 
            currentTargetX = gameWidth - SAFE_MARGIN; 
            nearWall = true; 
        } else if (headX > gameWidth - SAFE_MARGIN) { 
            currentTargetX = SAFE_MARGIN; 
            nearWall = true; 
        }
        
        // Y축 경계 검사 및 목표 반대 방향 설정
        if (headY < SAFE_MARGIN) { 
            currentTargetY = gameHeight - SAFE_MARGIN; 
            nearWall = true; 
        } else if (headY > gameHeight - SAFE_MARGIN) { 
            currentTargetY = SAFE_MARGIN; 
            nearWall = true; 
        }

        // 2. 일정 시간마다 목표 무작위 변경 (벽에 가깝지 않을 경우)
        // 약 60프레임당 1% 확률로 목표를 변경하여 무작위 움직임을 유도
        if (!nearWall && Math.random() < 0.01) { 
             currentTargetX = (int) (Math.random() * gameWidth);
             currentTargetY = (int) (Math.random() * gameHeight);
        }
        
        // 3. 결정된 목표로 이동 (Worm의 기본 이동 로직 재사용)
        super.move(currentTargetX, currentTargetY);
    }
}