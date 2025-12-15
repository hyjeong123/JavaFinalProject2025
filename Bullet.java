package Slither;

public class Bullet {
	// 변수 선언(총알의 x, y좌표 x, y이동속도, 총알크기 3, 총알이 경계 밖으로 나가는지 확인하는 boolean형 변수)
    private double x;
    private double y;
    private final double xVelocity;
    private final double yVelocity;
    private final int size = 3;
    private boolean bulletalive = true;
    // 총알 스피드는 10으로 고정
    private static final double SPEED = 10.0; 

    // 생성자 영역, 파라미터로 x, y, 각도를 받음
    public Bullet(double x, double y, double angle) {
        this.x = x;		// 총알의 x좌표
        this.y = y;		// 총알의 y좌표
        this.xVelocity = SPEED * Math.cos(angle);	// 총알의 x방향 이동속도
        this.yVelocity = SPEED * Math.sin(angle);	// 총알의 y방향 이동속도
    }
    // 총알의 이동함수 
    public void move() {
        x += xVelocity;		// 좌표 + 속도
        y += yVelocity;		// 좌표 + 속도
    }

    // x, y 좌표랑 총알 크기, 경계 밖으로 나갔는지 있는지 확인하는 함수
    public double getX() { 
    		return x; 
    	}
    public double getY() { 
    		return y; 
    	}
    public int getIntX() { 
    		return (int) Math.round(x); 
    	}
    public int getIntY() { 
    		return (int) Math.round(y); 
    	}
    public int getSize() { 
    		return size; 
    	}
    public boolean bulletAlive() { 
    		return bulletalive; 
    	}
    public void bulletDead() { 
    		bulletalive = false; 
    	}

    // 총알이 화면 밖으로 나갔는지 확인하는 함수
    public boolean outofRange(int gameWidth, int gameHeight) {
        return x < -50 || x > gameWidth + 50 || y < -50 || y > gameHeight + 50;	// x, y가 나갔는지 확인되면 true를 반환하는 함수
    }
}