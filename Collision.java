package Slither;

public class Collision {

    // 1) 점과 원 충돌 (총알 - 지렁이 몸통 충돌)
    public static boolean pointCircle(int px, int py, int cx, int cy, int radius) {
        int dx = px - cx;
        int dy = py - cy;
        return dx * dx + dy * dy <= radius * radius;
    }

    // 2) 두 원 충돌 (지렁이 머리 - 벽, 머리 - 몸통, 머리끼리 충돌)
    public static boolean circleCircle(int x1, int y1, int r1, int x2, int y2, int r2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int distanceSquared = dx * dx + dy * dy;
        int radiusSum = r1 + r2;
        return distanceSquared <= radiusSum * radiusSum;
    }

    // 3) 화면 밖(벽) 충돌 (머리가 화면 벗어나면 사망)
    public static boolean outOfBounds(int x, int y, int radius, int width, int height) {
        return x - radius < 0 || x + radius > width ||
               y - radius < 0 || y + radius > height;
    }
}
