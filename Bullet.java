package Slither;

public class Bullet {
    public int x, y;
    public int dx, dy;   // 방향
    public int speed = 20;
    public boolean alive = true;

    public Bullet(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;

        double vx = targetX - x;
        double vy = targetY - y;
        double dist = Math.sqrt(vx*vx + vy*vy);

        dx = (int) ((vx / dist) * speed);
        dy = (int) ((vy / dist) * speed);
    }

    public void move() {
        x += dx;
        y += dy;
    }
}
