package Slither;

import java.util.ArrayList;
import java.util.List;

public class BotWorm extends Worm {
    
    private int currentTargetX;
    private int currentTargetY;
    private final int WORM_SEGMENT_RADIUS = 6;    
    private final int TARGET_REACHED_THRESHOLD_SQR = 100;
    
    public BotWorm(int x, int y, int size, int speed) {
        super(x, y, size, speed);
        this.currentTargetX = x + 100;
        this.currentTargetY = y;
    }
    
    public void move(PlayerWorm player, ArrayList<Food> foodList, ArrayList<BotWorm> allBots, int gameWidth, int gameHeight) {
        
        Worm.Circle head = getHead();
        if (head == null) return;
        
        int headX = head.getIntX();
        int headY = head.getIntY();

        // 1. 목표 도달 확인 및 초기화 로직
        double dx = headX - currentTargetX;
        double dy = headY - currentTargetY;
        boolean targetReached = false;
        
        if (dx * dx + dy * dy < TARGET_REACHED_THRESHOLD_SQR) {
            targetReached = true;
            currentTargetX = -1; // 목표를 무효화
            currentTargetY = -1;
        }

        // 2. **[AI 우선순위 1] 벽 회피 로직**
        int SAFE_MARGIN = 50 + WORM_SEGMENT_RADIUS;    
        boolean nearWall = false;
        
        if (headX < SAFE_MARGIN) {    
            currentTargetX = gameWidth - SAFE_MARGIN;    
            nearWall = true;    
        } else if (headX > gameWidth - SAFE_MARGIN) {    
            currentTargetX = SAFE_MARGIN;    
            nearWall = true;    
        }
        
        if (headY < SAFE_MARGIN) {    
            currentTargetY = gameHeight - SAFE_MARGIN;    
            nearWall = true;    
        } else if (headY > gameHeight - SAFE_MARGIN) {    
            currentTargetY = SAFE_MARGIN;    
            nearWall = true;    
        }

        // 3. **[AI 우선순위 2] 먹이 추적 및 무작위 이동 로직**
        if (!nearWall && (targetReached || currentTargetX == -1)) {
              Food closestFood = findClosestFood(headX, headY, foodList);
              
              if (closestFood != null) {
                  // 가장 가까운 먹이로 목표 설정
                  currentTargetX = closestFood.getX();
                  currentTargetY = closestFood.getY();
              } else if (currentTargetX == -1) {    
                  // 먹이가 없고 목표도 없으면 무작위 목표 설정
                  if (Math.random() < 0.05) {    
                       currentTargetX = (int) (Math.random() * gameWidth);
                       currentTargetY = (int) (Math.random() * gameHeight);
                  }
              }
        }
        
        // 4. 결정된 목표로 이동
        super.move(currentTargetX, currentTargetY);
    }

    // 가장 가까운 먹이를 찾는 헬퍼 메서드
    private Food findClosestFood(int currentX, int currentY, ArrayList<Food> foodList) {
        Food closest = null;
        double minDistanceSq = Double.MAX_VALUE;

        for (Food f : foodList) {
            double dx = f.getX() - currentX;
            double dy = f.getY() - currentY;
            double distanceSq = dx * dx + dy * dy;

            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                closest = f;
            }
        }
        return closest;
    }
}