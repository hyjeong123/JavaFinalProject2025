package Slither;

import java.util.ArrayList;
import java.util.List;


public class Worm {
	// 지렁이의 몸통을 구성하는 원에 대한 정보를 가진 클래스 Circle
    public class Circle {
        private final double x, y;	// 이때 왜 final로 좌표를 선언하냐면, circle해서 만든 원의 위치는 고정되고 프레임이 바뀔때 새로운 원을 그리므로 고정된다(set해서 다시 x, y를 바꾸는거 아님)
        // 생성자 영역: 파라미터는 원의 x, y좌표이다
        public Circle(double x, double y) { 
        	this.x = x; 
        	this.y = y; 
        }
        // 원의 x, y좌표를 가지는 get함수 실수형, 정수형 두가지 있음
        public double getX() { 
        	return x; 
        }
        public double getY() { 
        	return y; 
        }
        public int getIntX() { 
        	return (int)Math.round(x); 
        }
        public int getIntY() { 
        	return (int)Math.round(y); 
        }
    }
    
    // 전역변수
    private double x, y;	// 지렁이의 x, y좌표
    private double angle = 0; // 지렁이의 각도(초기 설정 = 0)
    private int speed;	// 속도
    private final List<Circle> body = new ArrayList<>();	// 원에 대한 ArrayList를 body라는 이름의 인스턴스로 생성한다
    
    // 생성자 영역, 파라미터는 x, y, size, speed를 나타냄
    public Worm(int x, int y, int size, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        // size만큼 원을 그린다
        for (int i = 0; i < size; i++) {
            // 초기 몸통 생성
            body.add(new Circle(x, y));
        }
    }
    
    // 움직임에 대한 함수
    public void move(int second_X, int second_Y) {
        // 지렁이의 방향 구하기(방향 벡터 곱하기 speed 이용), 각도 계산
        double dx = second_X - x;
        double dy = second_Y - y;
        
        // 이동거리가 1이상 되었을때 계산하라
        if (dx * dx + dy * dy > 1) { 
            angle = Math.atan2(dy, dx); 	// 각도를 아크탄젠트로 구한다
        }

        // 새로운 머리 위치 계산
        double new_X = x + Math.cos(angle) * speed;
        double new_Y = y + Math.sin(angle) * speed;
        
        // 3. 머리 추가
        body.add(0, new Circle(new_X, new_Y));
        
        // 4. 길이가 늘어나지 않도록 이동할 때마다 꼬리를 무조건 제거합니다.
        if (!body.isEmpty()) {
            body.remove(body.size() - 1);
        }
        
        // 지렁이의 x, y좌표 업데이트
        this.x = new_X;	
        this.y = new_Y;
    }

    // 원의 머리부분을 찾는 함수
    public Circle getHead() {
        if (body.isEmpty()) {
        	return null;
        }
        else {
        	return body.get(0);
        }
    }
    
    // x, y좌표의 get함수
    public double getX() { 
    		return x; 
    	}
    public double getY() { 
    		return y; 
    	}
    
    // Circle에 대한 ArrayList를 반환한다
    public List<Circle> getBody() { 
    		return body; 
    	}
    
    
    // 지렁이의 몸통이 증가할 경우 함수
    public void increase() { 
        // 먹이를 먹었을 때 길이를 1 늘립니다. (꼬리 위치에 새 세그먼트 추가)
        if (!body.isEmpty()) {	// 만약 몸통이 비어있지 않다면
             Circle tail = body.get(body.size() - 1);	// 꼬리부분(마지막 index)의 body 객체를 가져와
             body.add(new Circle(tail.getX(), tail.getY()));	// body ArrayList에 추가한다
        }
    }
    
    // 지렁이의 몸통이 감소할 경우의 함수
    public void decrease(int segments) {
    	// 몸 길이만큼 그리되
        for (int i = 0; i < segments; i++) {
            if (!body.isEmpty()) {	// 만약 몸통이 비어있지 않다면
                body.remove(body.size() - 1);	// 맨 마지막 index의 원을 하나 없앤다
            } else {
                break;
            }
        }
    }
    
    // 크기, 이동속도, 각도에 대한 get/set함수
    public int getSize() { 
    		return body.size(); 
    	}
    public void setSpeed(int speed) { 
    	this.speed = speed; 
    }
    
    public double getangle() {
        return angle;
    }
}