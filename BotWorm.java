package Slither;

import java.util.ArrayList;

// Worm클래스의 기능을 받기 위해 상속받음
public class BotWorm extends Worm {
    // 전역 변수들
    private int target_x;			// 봇 지렁이가 이동해야할 x좌표
    private int target_y;			// 봇 지렁이가 이동해야할 y좌표
    private final int RADIUS = 6;    // 지렁이 몸통을 이루는 원 하나의 반지름 = 6
    private final int TARGET_DISTANCE = 100;	// 목표지점까지의 거리 제곱의 값을 100으로 지정, 100보다 작으면 목표에 도달했다고 여김
    
    private final int SPEED;	// 기본 속도 저장(3)
    private static final int BOOSTSPEED = 5; // 부스트 시 속도(5)
    private static final int BOOSTDURATION = 180; // 부스트 지속 시간 3초
    private int boosttimer = 0; // 남은 부스트 시간 카운터
    
    // 생성자 영역(파라미터 x, y, 크기, 이동속도)
    public BotWorm(int x, int y, int size, int speed) {
        super(x, y, size, speed);	// Worm클래스의 생성자를 호출하여 초기의 설정값으로 저장
        
        SPEED = speed; 			    // 기본속도 저장(5)
        target_x = x + 100;			// 초기 목표값의 x좌표는 지정한 x + 100의 값임
        target_y = y;				// 초기 목표값의 y좌표는 지정한 y값과 동일
    }
    
    // 움직임에 관한 함수 move영역(파라미터 플레이어객체, food객체, botworm객체, 게임판의 폭과 높이)
    // ※ move메소드는 AI의 우선순위에 따라 이동해야할 목표를 정한다
    public void move(PlayerWorm player, ArrayList<Food> foodList, ArrayList<BotWorm> botworms, int gameWidth, int gameHeight) {
    	// 지렁이의 머리부분을 가져온다
        Worm.Circle head = getHead();
        if (head == null) {				// 머리가 비어있다면 죽은것으로 처리해 아무것도 하지 않고 종료
        	return;
        }
        
        int headX = head.getIntX();		// 머리부분의 정수형 x, y의 값을 가져온다(그리기 위함)
        int headY = head.getIntY();

        // 랜덤한 시간마다 3초동안 돌진하게 만들기
        if (boosttimer > 0) {
            boosttimer--; 
            
            // 부스트 종료 시 속도 원상 복구
            if (boosttimer == 0) {
                // 상위 클래스의 setSpeed(int) 메서드가 있다고 가정하고 호출
                super.setSpeed(SPEED); 
            }
        } else {
            // 부스트 상태가 아닐 경우
            if (Math.random() < 0.01) { 	// Math.random()해서 나온 숫자가 0.01미만(1프로 미만)일때
                boosttimer = BOOSTDURATION;
                // 속도를 부스트 속도로 변경
                super.setSpeed(BOOSTSPEED); 
            }
        }
        // ----------------------------------------------------
        
        // 목표 도달 확인 및 초기화 로직
        double dx = headX - target_x;	// 현위치와 목표위치에 대한 x, y의 변화량을 구한다
        double dy = headY - target_y;	// dx, dy는 변화량을 저장하는 변수 델타값임
        boolean targetreached = false;	// 목표에 도달했는지 확인하는 boolean형 변수를 false로 초기화한다
        
        // 점과 점 사이의 거리 공식을 이용해 목표 거리보다 작으면 도달했다고 판단
        if (dx * dx + dy * dy < TARGET_DISTANCE) {
            targetreached = true;	// targetreached를 true로 바꿈
            target_x = -1; // 목표를 무효화(-1은 없는 좌표)
            target_y = -1;
        }

        // ai우선순위 첫번째, 벽 회피 조직
        int SAFEAREA = 50 + RADIUS;    // 봇의 머리가 벽과 가까워졌다고 판단하는 영역 (56)
        boolean nearwall = false;		  // 아직 가까워진게 아니므로 false로 초기화
        
        // x, y좌표에 대하여 안전영역에 들어왔는지 안 들어왔는지 확인
        if (headX < SAFEAREA) {    
            target_x = gameWidth - SAFEAREA;    
            nearwall = true;    
        } else if (headX > gameWidth - SAFEAREA) {    
            target_x = SAFEAREA;    
            nearwall = true;    
        }
        
        if (headY < SAFEAREA) {    
            target_y = gameHeight - SAFEAREA;    
            nearwall = true;    
        } else if (headY > gameHeight - SAFEAREA) {    
            target_y = SAFEAREA;    
            nearwall = true;    
        }
        // 게임판의 영역 - 안전영역을 뺀 구간에 x, y좌표가 들어온다면 벽 근처에 있다고 판단하고 목표좌표를 안전구역으로 설정해야함
        
        
        // ai우선순위 두번째, 먹이 추적 및 무작위 이동 로직
        if (!nearwall && (targetreached || target_x == -1)) {	// 벽 근처가 아니고 목표에 도달했거나 목표가 무효화됐을때 이 조건을 실행
              Food closestfood = findClosestFood(headX, headY, foodList);	// 가장 가까운 먹이를 찾는 함수를 불러오고 그 함수를 이용한 Food인스턴스 생성
              // 만약 가장 가까운 먹이가 없다면
              if (closestfood != null) {
                  // 가장 가까운 먹이로 목표 설정
                  target_x = closestfood.getX();
                  target_y = closestfood.getY();
              } else if (target_x == -1) {    
                  // 먹이가 없고 목표도 없으면 무작위 목표 설정
                  if (Math.random() < 0.05) {    // 이 수치가 있는 이유는 매 프레임마다 계속 target좌표가 바뀌면 봇이 멍청해진다
                       target_x = (int) (Math.random() * gameWidth);
                       target_y = (int) (Math.random() * gameHeight);
                  }
              }
        }
        
        // 결정된 목표로 이동시키기
        super.move(target_x, target_y);
    }

 // 가장 가까운 먹이를 찾게 도와주는 메소드(파라미터 목표x, y좌표 Food인스턴스들)
    // 참고: 이 로직은 봇의 현재 머리가 아닌 target_x, target_y를 기준으로 탐색합니다.
    private Food findClosestFood(int target_x, int target_y, ArrayList<Food> foods) {
        
        // 봇의 시야 범위 (250px. 250*250 = 62500)
        final double BOTSIGHT = 62500.0;
        
        // 시야 내의 먹이를 저장할 리스트
        ArrayList<Food> visibleFoods = new ArrayList<>();
        
        Food closest = null;	// 가장 가까운 먹이가 없음
        double mindistance = Double.MAX_VALUE; // 터무니 없는 큰 수치 Double.MAX_VALUE 사용 

        for (Food f : foods) {		// 모든 먹이 객체를 foods 객체의 개수만큼 순회한다
            double dx = f.getX() - target_x;	// 먹이의 x좌표와 목표지점 x좌표의 변화량을 구한다
            double dy = f.getY() - target_y;	// 먹이의 x좌표와 목표지점 y좌표의 변화량을 구한다
            double fooddistance = dx * dx + dy * dy;	// 먹이의 거리 제곱 구함
            
            // 시야 범위 내에 들어온 먹이만 따로 리스트에 수집
            if (fooddistance < BOTSIGHT) {
                 visibleFoods.add(f);
            }
            
            // 가장 가까운 먹이를 찾는 로직)
            if (fooddistance < mindistance) {	// 만약 먹이의 거리가 최소 거리보다 작으면 업데이트
                mindistance = fooddistance;
                closest = f;	// 가장 가까운 먹이의 Food인스턴스를 f로 설정함
            }
        }
        
        // 충돌 방지 핵심 로직 (시야 내의 먹이 중 무작위 선택)      
        if (visibleFoods.isEmpty()) {
            // 시야 내에 먹이가 없다면, 기존처럼 (가장 가까웠던, 아마도 멀리 있는) 먹이를 반환한다
            return closest;
        } else {
            // 시야 내에 먹이가 있다면, 그 먹이들 중 무작위로 하나를 선택하여 반환한다
            int randomIndex = (int) (Math.random() * visibleFoods.size());
            return visibleFoods.get(randomIndex);
        }
    }
}