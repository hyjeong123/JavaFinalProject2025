package Slither;

import java.util.ArrayList;

public class Worm {
    public int x, y;          // 머리 좌표
    public int speed = 5; 	  // 스피드
    
    // 몸을 구성하는 원 중 머리의 원, 생성자는 (x, y)에 원을 생성한다
    public class Circle {
        public int x, y;
        public Circle(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    // body 객체를 ArrayList로 만든다, body 가변적 배열 생성
    public ArrayList<Circle> body = new ArrayList<>();
    
    // 생성자 영역, size크기만큼 지렁이를 만드는 생성자 Worm
    public Worm(int startX, int startY, int size) {
        this.x = startX;
        this.y = startY;
               
        for (int i = 0; i < size; i++) {
            body.add(new Circle(startX - (i * 10), startY));
        }
        		
    }

    // 뒤의 도트들이 따라오는 함수
    public void move(int targetX, int targetY) {
        // 머리 방향 계산(벡터값)
        double directionx = targetX - x;
        double directiony = targetY - y;
        double dist = Math.sqrt(directionx * directionx + directiony * directiony);
        
        if (dist != 0) {
            x += (directionx / dist) * speed;
            y += (directiony / dist) * speed;
        }

        // 몸통 위치 업데이트 (앞이 뒤로 따라오게)
        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).x = body.get(i - 1).x;
            body.get(i).y = body.get(i - 1).y;
        }

        // 머리 위치를 body[0]에 반영
        body.get(0).x = x;
        body.get(0).y = y;
    }
    
    // 총알 맞을때
    public boolean hit() {
        if (body.size() > 1) {
            body.remove(body.size() - 1);  // 꼬리 제거
            return false; // 아직 안죽음
        } else {
            return true; // 죽음
        }
    }
}
