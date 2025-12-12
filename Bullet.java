package Slither;

public class Bullet {
    private double x;
    private double y;
    private final double xVelocity;
    private final double yVelocity;
    private final int size = 3;
    private boolean isAlive = true;

    private static final double SPEED = 10.0; 

    // GamePanel에서 요구하는 생성자 (x, y, angle)
    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.xVelocity = SPEED * Math.cos(angle);
        this.yVelocity = SPEED * Math.sin(angle);
    }

    public void move() {
        x += xVelocity;
        y += yVelocity;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getIntX() { return (int) Math.round(x); }
    public int getIntY() { return (int) Math.round(y); }
    public int getSize() { return size; }
    
    public boolean isAlive() { return isAlive; }
    public void setDead() { isAlive = false; }

    // 총알이 화면 밖으로 나갔는지 확인하는 헬퍼 메서드
    public boolean isOutsideBounds(int gameWidth, int gameHeight) {
        return x < -50 || x > gameWidth + 50 || y < -50 || y > gameHeight + 50;
    }
}