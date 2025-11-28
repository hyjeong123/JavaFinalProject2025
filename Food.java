package Slither;

public class Food {

    public int x;
    public int y;
    public int size = 8;   // 먹이 반지름
    
    public Food(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isEaten(int headX, int headY) {
        int dx = headX - x;
        int dy = headY - y;
        int distSq = dx*dx + dy*dy;

        return distSq < (size + 5) * (size + 5);
    }
}
