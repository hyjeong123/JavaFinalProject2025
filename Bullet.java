package Slither;

public class Bullet {
    
    private double x, y;
    private final double dx, dy; // 방향 벡터는 double로 저장
    private final int speed = 20;
    private boolean alive = true; 

    public Bullet(double x, double y, int targetX, int targetY) {
        this.x = x;
        this.y = y;

        double vx = targetX - x;
        double vy = targetY - y;
        double dist = Math.sqrt(vx*vx + vy*vy);

        if (dist != 0) {
            this.dx = (vx / dist) * speed;
            this.dy = (vy / dist) * speed;
        } else {
            this.dx = 0;
            this.dy = 0;
        }
    }

    public void move() {
        x += dx;
        y += dy;
    }

    // Getter/Setter
    public int getIntX() { 
    	return (int)x; 
    	}
    public int getIntY() { 
    	return (int)y; 
    	}

    public double getX() { 
    	return x; 
    	}
    public double getY() { 
    	return y; 
    	}
    
    public boolean isAlive() { 
    	return alive; 
    	}
    public void setDead() { this.alive = false; } 
    
    // 경계 체크
    public boolean isOutsideBounds(int w, int h) {
        return x < -20 || x > w + 20 || y < -20 || y > h + 20;
    }
}