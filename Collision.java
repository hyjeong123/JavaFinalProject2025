package Slither;

import java.util.ArrayList;
import java.util.List;

public class Collision {
    
    private static final int WORM_SEGMENT_RADIUS = 6;
    private static final int FOOD_RADIUS = 8;
    private static final int BULLET_RADIUS = 3;
    
    private static final double WORM_WORM_COLLISION_SQR = (WORM_SEGMENT_RADIUS * 2) * (WORM_SEGMENT_RADIUS * 2); 
    private static final double WORM_FOOD_COLLISION_SQR = (WORM_SEGMENT_RADIUS + FOOD_RADIUS) * (WORM_SEGMENT_RADIUS + FOOD_RADIUS); 
    private static final double BULLET_WORM_COLLISION_SQR = (BULLET_RADIUS + WORM_SEGMENT_RADIUS) * (BULLET_RADIUS + WORM_SEGMENT_RADIUS); 


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

    // 🚨 [수정된 부분] 즉시 게임 종료 방지를 위한 로직 추가
    public static boolean hitSelf(PlayerWorm player) {

        // 길이가 10 미만이면 자기 충돌 체크를 아예 건너뜁니다. 
        // 지렁이가 충분히 길어져서 세그먼트가 분리될 때까지 기다립니다.
        if (player.getBody().size() < 10) {
            return false;
        }

        Worm.Circle head = player.getHead();
        if (head == null) return false;

        List<?> body = player.getBody();

        // 몸통 6번째 세그먼트(인덱스 6)부터 충돌을 체크합니다. (추가 안전성)
        for (int i = 6; i < body.size(); i++) {
            Worm.Circle c = (Worm.Circle) body.get(i);
            
            double dx = head.getX() - c.getX();
            double dy = head.getY() - c.getY();
            
            if (dx*dx + dy*dy < WORM_WORM_COLLISION_SQR) {
                return true;
            }
        }
        return false;
    }

    public static boolean hitBots(PlayerWorm player, ArrayList<BotWorm> bots) {

        Worm.Circle head = player.getHead();
        if (head == null) return false;
        
        for (BotWorm bw : bots) {
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
    
    public static BotWorm checkHitBotWithBullet(Bullet bullet, ArrayList<BotWorm> bots) {
        
        for (BotWorm bw : bots) {
            for (Worm.Circle c : (List<Worm.Circle>) bw.getBody()) {
                
                double dx = bullet.getX() - c.getX();
                double dy = bullet.getY() - c.getY();
                
                if (dx*dx + dy*dy < BULLET_WORM_COLLISION_SQR) {
                    return bw;
                }
            }
        }
        return null;
    }
}