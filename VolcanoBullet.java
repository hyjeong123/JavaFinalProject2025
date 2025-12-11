package Slither;

public class VolcanoBullet {
    private double x;
    private double y;
    private final double xVelocity;
    private final double yVelocity;
    private final int size = 5;
    private boolean isAlive = true;

    private static final double SPEED = 5.0; 

    public VolcanoBullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.xVelocity = SPEED * Math.cos(angle);
        this.yVelocity = SPEED * Math.sin(angle);
    }

    public void move() {
        x += xVelocity;
        y += yVelocity;
    }

    public int getIntX() { return (int) Math.round(x); }
    public int getIntY() { return (int) Math.round(y); }
    public int getSize() { return size; }
    
    public boolean isAlive() { return isAlive; }
    public void setDead() { isAlive = false; }
}