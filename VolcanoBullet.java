package Slither;

public class VolcanoBullet {
	// 화산탄의 x, y좌표 x, y축 속도 화산탄 크기(5), 화산탄이 화면에 남아있는지 확인하는 변수 선언
    private double x;
    private double y;
    private final double xVelocity;
    private final double yVelocity;
    private final int size = 5;
    private boolean volcanobulletalive = true;
    // 화산탄 고정 이동속도 5
    private static final double SPEED = 5.0; 
    // 생성자 영역 (파라미터 x, y, 각도)
    public VolcanoBullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.xVelocity = SPEED * Math.cos(angle);
        this.yVelocity = SPEED * Math.sin(angle);
    }
    // 움직임 관리하는 함수 move
    public void move() {
        x += xVelocity;
        y += yVelocity;
    }
    // int형을 받는 이유는 화면의 좌표는 정수로 설정해야 하기 때문
    public int getIntX() { 
    	return (int) Math.round(x); 
    }
    public int getIntY() { 
    	return (int) Math.round(y); 
    }
    public int getSize() { 
    	return size; 
    }
    // 총알이 남아있는지 확인하는 boolean형 함수
    public boolean bulletAlive() { 
    	return volcanobulletalive; 
    }
    // 만약 총알이 없다면 volcanobulletalive 변수를 false로 바꿈
    public void bulletDead() { 
    	volcanobulletalive = false; 
    }
}