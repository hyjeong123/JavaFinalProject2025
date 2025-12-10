// Collision.java 전체 코드 (이 코드를 사용해주세요)

package Slither;

import java.util.ArrayList;
import java.util.List;

public class Collision {
    
    // 상수 정의 (WORM_WORM_COLLISION_SQR 등)
    private static final int WORM_SEGMENT_RADIUS = 6;
    private static final int FOOD_RADIUS = 8;
    private static final int BULLET_RADIUS = 3;
    
    private static final double WORM_WORM_COLLISION_SQR = (WORM_SEGMENT_RADIUS * 2) * (WORM_SEGMENT_RADIUS * 2); // 144
    private static final double WORM_FOOD_COLLISION_SQR = (WORM_SEGMENT_RADIUS + FOOD_RADIUS) * (WORM_SEGMENT_RADIUS + FOOD_RADIUS); // 196
    private static final double BULLET_WORM_COLLISION_SQR = (BULLET_RADIUS + WORM_SEGMENT_RADIUS) * (BULLET_RADIUS + WORM_SEGMENT_RADIUS); // 81


    public static Food checkEatFood(PlayerWorm player, ArrayList<Food> foods) {
        Worm.Circle head = player.getHead();
        if (head == null) return null;

        for (Food f : foods) {
            double dx = head.getX() - f.getX();
            double dy = head.getY() - f.getY();
            double distSq = dx*dx + dy*dy; 

            if (distSq < WORM_FOOD_COLLISION_SQR) { 
                return f;
            }
        }
        return null;
    }

    public static boolean hitWall(PlayerWorm player, int w, int h) {
        Worm.Circle head = player.getHead();
        if (head == null) return false;
        
        return (head.getIntX() - WORM_SEGMENT_RADIUS < 0 || 
                head.getIntX() + WORM_SEGMENT_RADIUS > w || 
                head.getIntY() - WORM_SEGMENT_RADIUS < 0 || 
                head.getIntY() + WORM_SEGMENT_RADIUS > h);
    }

    // [기존 로직] 플레이어 머리가 봇의 몸통/머리에 닿는지 체크 (플레이어 사망 조건)
    public static boolean hitBots(PlayerWorm player, ArrayList<BotWorm> bots) {
        Worm.Circle head = player.getHead();
        if (head == null) return false;
        
        for (BotWorm bw : bots) {
            // 봇의 모든 몸통 세그먼트를 순회 (머리 포함)
            for (Worm.Circle c : (List<Worm.Circle>) bw.getBody()) { 
                double dx = head.getX() - c.getX();
                double dy = head.getY() - c.getY();

                if (dx*dx + dy*dy < WORM_WORM_COLLISION_SQR) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // [새 로직] 봇 머리가 플레이어 몸통에 닿는지 체크 (봇 사망 조건)
    public static BotWorm checkBotHitPlayerBody(PlayerWorm player, ArrayList<BotWorm> bots) {
        List<Worm.Circle> playerBody = (List<Worm.Circle>) player.getBody();
        if (playerBody.size() <= 1) return null;
        
        // 플레이어 몸통에서 인덱스 1부터 순회 (머리(인덱스 0)는 제외)
        for (int i = 1; i < playerBody.size(); i++) {
            Worm.Circle playerSegment = playerBody.get(i);
            
            for (BotWorm bw : bots) {
                Worm.Circle botHead = bw.getHead();
                if (botHead == null) continue;

                double dx = botHead.getX() - playerSegment.getX();
                double dy = botHead.getY() - playerSegment.getY();
                
                if (dx * dx + dy * dy < WORM_WORM_COLLISION_SQR) {
                    return bw; 
                }
            }
        }
        return null;
    }

    // [수정된 로직] 총알과 봇 충돌 체크 (봇의 몸통만 체크, 머리 제외)
    public static BotWorm checkHitBotWithBullet(Bullet bullet, ArrayList<BotWorm> bots) {
        
        for (BotWorm bw : bots) {
            List<Worm.Circle> body = (List<Worm.Circle>) bw.getBody();
            if (body.size() <= 1) continue; 
            
            // 인덱스 1부터 순회 (머리(인덱스 0) 건너뛰기)
            for (int i = 1; i < body.size(); i++) {
                Worm.Circle segment = body.get(i);
                
                double dx = bullet.getX() - segment.getX();
                double dy = bullet.getY() - segment.getY();
                
                if (dx*dx + dy*dy < BULLET_WORM_COLLISION_SQR) {
                    return bw; 
                }
            }
        }
        return null;
    }
}