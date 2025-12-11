package Slither;

import java.util.ArrayList;
import java.util.List;

public abstract class Worm {
    
    public class Circle {
        private final double x, y;
        public Circle(double x, double y) { this.x = x; this.y = y; }
        public double getX() { return x; }
        public double getY() { return y; }
        public int getIntX() { return (int)Math.round(x); }
        public int getIntY() { return (int)Math.round(y); }
    }

    private double x, y;
    private double currentAngle = 0; // 뱀의 실제 이동 각도
    private int speed;
    private final List<Circle> body = new ArrayList<>();
    // private final double DISTANCE_BETWEEN_SEGMENTS = 6.0; // 사용되지 않음

    public Worm(int x, int y, int size, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        for (int i = 0; i < size; i++) {
            // 초기 몸통 생성 시 좌표를 미세하게 조정하여 뱀처럼 보이게 할 수 있으나, 일단 동일 좌표로 초기화
            body.add(new Circle(x, y));
        }
    }

    public void move(int targetX, int targetY) {
        // 1. 목표 방향 계산 및 currentAngle 업데이트
        double dx = targetX - x;
        double dy = targetY - y;
        
        if (dx * dx + dy * dy > 1) { 
            currentAngle = Math.atan2(dy, dx); 
        }

        // 2. 새로운 머리 위치 계산
        double newX = x + Math.cos(currentAngle) * speed;
        double newY = y + Math.sin(currentAngle) * speed;
        
        // 3. 머리 추가
        body.add(0, new Circle(newX, newY));
        
        // 4. 🐍 핵심 수정: 길이가 늘어나지 않도록 이동할 때마다 꼬리를 무조건 제거합니다.
        // 먹이를 먹었을 때 (increase() 호출)에만 길이가 늘어납니다.
        if (!body.isEmpty()) {
            body.remove(body.size() - 1);
        }
        
        this.x = newX;
        this.y = newY;
    }

    public Circle getHead() {
        return body.isEmpty() ? null : body.get(0);
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public List<?> getBody() { return body; }
    
    // 💡 increase()가 호출되면 move()에서 꼬리 제거를 한 번 건너뛰는 효과가 발생하여 길이가 늘어납니다.
    public void increase() { 
        // Worm.move()가 한 프레임에 한번 실행될 때, 이 increase()를 호출하면
        // 다음 move()가 body.remove()를 실행하기 전에 새 세그먼트가 추가되어 길이가 늘어납니다.
        // 명시적으로 세그먼트를 추가하여 길이를 늘리도록 구현합니다.
        // body.add(new Circle(x, y)); // 꼬리 위치에 추가하면 렌더링 시 자연스러움
        if (!body.isEmpty()) {
             Circle tail = body.get(body.size() - 1);
             body.add(new Circle(tail.getX(), tail.getY()));
        }
    }
    
    // 화산탄 피격용 길이 감소 메서드
    public void decrease(int segments) {
        for (int i = 0; i < segments; i++) {
            if (!body.isEmpty()) {
                body.remove(body.size() - 1);
            } else {
                break;
            }
        }
    }
    
    public int getSize() { return body.size(); }
    public void setSpeed(int speed) { this.speed = speed; }
    
    public double getCurrentAngle() {
        return currentAngle;
    }
}