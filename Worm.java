package Slither;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Worm {

    private double x, y;
    private int speed;
    private final ArrayList<Circle> body = new ArrayList<>();

    // Collision에서 접근 가능하도록 public static으로 선언하고, 필드는 private final로 캡슐화
    public static class Circle { 
        private final double x, y;
        
        public Circle(double x, double y) { this.x = x; this.y = y; }
        
        // Collision과 GamePanel에서 사용하는 Getter
        public int getIntX() { return (int)x; } 
        public int getIntY() { return (int)y; }
        public double getX() { return x; }
        public double getY() { return y; }
    }
    
    // Worm의 위치 Getter
    public double getX() {
    	return this.x;
    }
    public double getY() {
    	return this.y;
    }
    
    // speed Getter/Setter
    public void setSpeed(int s) {
        this.speed = s;
    }

    public int getSpeed() {
        return this.speed;
    }
    
    // Collision과 GamePanel에서 사용하는 Body Getter
    public List<Circle> getBody() {
        // 외부에서의 수정을 방지하기 위해 UnmodifiableList 반환
        return Collections.unmodifiableList(body);
    }
    
    public Circle getHead() {
        if (body.isEmpty()) return null;
        return body.get(0);
    }
    
    public Worm(int x, int y, int size, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;

        for (int i = 0; i < size; i++) {
            body.add(new Circle(x, y)); 
        }
    }

    // 지렁이 이동 및 방향 계산
    public void move(int targetX, int targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist != 0) {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }

        // 몸통 업데이트 로직: 새 머리 추가, 꼬리 제거
        body.add(0, new Circle(x, y));
        body.remove(body.size() - 1);
    }

    // 길이 증가
    public void increase() {
        // 현재 꼬리 위치에 새 세그먼트 추가
        if (!body.isEmpty()) {
            Circle tail = body.get(body.size() - 1);
            body.add(new Circle(tail.getX(), tail.getY()));
        }
    }

    // 길이 감소 (총알 피격 시 사용)
    public void decrease() {
        if (body.size() > 1) {
            body.remove(body.size() - 1);
        }
    }
}