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

    public static Food checkEatFood(Worm worm, ArrayList<Food> foods) {
        Worm.Circle head = worm.getHead();
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
    
    public static BotWorm checkBotHitPlayerBody(PlayerWorm player, ArrayList<BotWorm> bots) {
        List<Worm.Circle> playerBody = (List<Worm.Circle>) player.getBody();
        if (playerBody.size() <= 1) return null;
        
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

    public static BotWorm checkBotHitBot(ArrayList<BotWorm> bots) {
        for (int i = 0; i < bots.size(); i++) {
            BotWorm currentBot = bots.get(i);
            Worm.Circle head = currentBot.getHead();
            if (head == null) continue;
            
            for (int j = 0; j < bots.size(); j++) {
                if (i == j) continue;
                
                BotWorm otherBot = bots.get(j);
                List<Worm.Circle> otherBody = (List<Worm.Circle>) otherBot.getBody();
                
                for (Worm.Circle segment : otherBody) {
                    double dx = head.getX() - segment.getX();
                    double dy = head.getY() - segment.getY();
                    
                    if (dx * dx + dy * dy < WORM_WORM_COLLISION_SQR) {
                        return currentBot;    
                    }
                }
            }
        }
        return null;
    }

    public static BotWorm checkHitBotWithBullet(Bullet bullet, ArrayList<BotWorm> bots) {
        
        for (BotWorm bw : bots) {
            List<Worm.Circle> body = (List<Worm.Circle>) bw.getBody();
            // 총알은 머리(0)를 제외한 몸통에 맞아야 함
            if (body.size() <= 1) continue;    
            
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
    
 // Collision.java: checkHitWormWithVolcanoBullet 메서드 (전체 몸통 충돌 체크)

    public static Worm checkHitWormWithVolcanoBullet(VolcanoBullet vb, PlayerWorm player, ArrayList<BotWorm> bots) {
        
        double vx = vb.getIntX();
        double vy = vb.getIntY();
        
        // 충돌 허용 거리 (화산탄 크기 + 지렁이 세그먼트 반지름)의 제곱
        double radiusSq = (vb.getSize() + WORM_SEGMENT_RADIUS) * (vb.getSize() + WORM_SEGMENT_RADIUS);    

        // 1. 플레이어 충돌 체크 (모든 몸통 세그먼트 체크)
        for (Worm.Circle segment : (List<Worm.Circle>) player.getBody()) {
            double dx = vx - segment.getX();
            double dy = vy - segment.getY();
            if (dx * dx + dy * dy < radiusSq) {
                return player; // 몸통에 맞았을 경우 플레이어 반환
            }
        }
        
        // 2. 봇 충돌 체크 (모든 몸통 세그먼트 체크)
        for (BotWorm bw : bots) {
            for (Worm.Circle segment : (List<Worm.Circle>) bw.getBody()) {
                double dx = vx - segment.getX();
                double dy = vy - segment.getY();
                if (dx * dx + dy * dy < radiusSq) {
                    return bw; // 몸통에 맞았을 경우 해당 봇 반환
                }
            }
        }
        return null;
    }

}