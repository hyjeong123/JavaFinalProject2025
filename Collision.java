package Slither;

import java.util.ArrayList;
import java.util.List;

public class Collision {
    
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

    // 🚨 [표준 로직] 플레이어 머리가 봇의 몸통 어디에 닿아도 사망
    public static boolean hitBots(PlayerWorm player, ArrayList<BotWorm> bots) {

        Worm.Circle head = player.getHead();
        if (head == null) return false;
        
        for (BotWorm bw : bots) {
            // 봇의 모든 몸통 세그먼트를 순회하며 플레이어 머리와 충돌 체크
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