package Slither;

import java.util.ArrayList;
import java.util.List;

public class Collision {
    // 전역변수 영역
	// 원 모양 객체의 반지름
    private static final int WORMRADIUS = 6;
    private static final int FOODRADIUS = 8;
    private static final int BULLETRADIUS = 3;
    
    // 객체 사이의 거리
    private static final double WORMWORM = (WORMRADIUS * 2) * (WORMRADIUS * 2); 	// (반지름 + 반지름)의 제곱으로 거리 구하기
    private static final double WORMFOOD = (WORMRADIUS + FOODRADIUS) * (WORMRADIUS + FOODRADIUS);
    private static final double WORMBULLET = (BULLETRADIUS + WORMRADIUS) * (BULLETRADIUS + WORMRADIUS); 
    // ※ 메소드 중 앞의 클래스명이 붙은 메소드들은 클래스로 만들어진 인스턴스를 반환하는 함수들이다
   
    // 먹이 섭취 함수 (파라미터 지렁이 객체, 먹이 객체)
    public static Food wormvsfood(Worm worm, ArrayList<Food> foods) {
        Worm.Circle head = worm.getHead();	// 지렁이의 머리부분을 가져온다(머리로 먹이를 먹기 때문) 
        if (head == null) return null;
        
        // 모든 먹이를 순회한다
        for (Food f : foods) {
        	// 먹이와 머리의 거리를 구해 먹이와 충돌하는지 확인
            double dx = head.getX() - f.getX();
            double dy = head.getY() - f.getY();
            double food_distance = dx*dx + dy*dy;    
            // 먹이와 머리사이의 거리가 최소 충돌거리보다 작으면 충돌한 food객체를 반환한다
            if (food_distance < WORMFOOD) {    
                return f;
            }
        }
        return null;	// 검사를 했는데 먹이 섭취가 없으면 null을 반환한다
    }
    
    
    // 지렁이와 플레이어의 부딪힘 확인을 하는 boolean형 함수(파라미터 플레이어 객체, width, height)
    // 이때 Worm객체가 아니라 PlayerWorm객체인 이유는 BotWorm은 벽에 부딪히지 않도록 했기 때문
    public static boolean playervswall(PlayerWorm player, int w, int h) {
        Worm.Circle head = player.getHead();	// player의 머리 들고옴
        if (head == null) { 
        	return false;
        }
        // 머리 좌표에 의해 벽에 부딪히는지 true / false형으로 확인해 반환한다
        return (head.getIntX() - WORMRADIUS < 0 ||  head.getIntX() + WORMRADIUS > w ||    
                head.getIntY() - WORMRADIUS < 0 ||  head.getIntY() + WORMRADIUS > h);
    }
    
    // 플레이어의 머리와 봇 지렁이들의 몸통이 충돌했는지 확인하는 함수(파라미터 플레이어 객체, 봇 지렁이 객체)
    public static boolean playerhitbotbody(PlayerWorm player, ArrayList<BotWorm> bots) {
    	Worm.Circle head = player.getHead();	// player의 머리 들고옴
        if (head == null) {
        	return false;
        }
        
        // 현재 생성되어있는 모든 BotWorm의 원형 몸통을 순회함
        for (BotWorm bw : bots) {
            for (Worm.Circle c : (List<Worm.Circle>) bw.getBody()) {    
                double dx = head.getX() - c.getX();		// 머리와 모든index의 원과의 거리를 측정했을때
                double dy = head.getY() - c.getY();
                // 만약 거리가 더 작다면 부딪힘을 true로 반환
                if (dx*dx + dy*dy < WORMWORM) {
                    return true;
                }
            }
        }
        return false;	// 아님 false반환
    }
    
    // BotWorm이 PlayerWorm에 부딪힐 경우를 확인하는 함수(파라미터 PlayerWorm 객체 BotWorm 객체)
    public static BotWorm bothitplayerbody(PlayerWorm player, ArrayList<BotWorm> bots) {        
    	List<Worm.Circle> playerBody = (List<Worm.Circle>) player.getBody();	// Worm.Circle타입의 객체를 담는 List, player의 몸통을 index순으로 반환한걸 저장
   
    	if (playerBody.size() <= 1) {	// 몸 길이가 1 이하(사망)
        	return null;
        }
        // player의 몸통과 충돌하였는지 검사하는 메소드(그래서 i = 1부터 시작함)
        for (int i = 1; i < playerBody.size(); i++) {
            Worm.Circle playerSegment = playerBody.get(i);	// 몸통을 index순으로 들고옴
            for (BotWorm bw : bots) {			// bw객체를 bots(생성된 지렁이의 수)수만큼 순회
                Worm.Circle botHead = bw.getHead();		// botHead는 bw의 머리를 가져옴
                if (botHead == null) {			// botHead가 없음(사망)이면 건너뜀
                	continue;
                }
                // 머리랑 봇 몸통을 이루는 원의 거리를 구한다
                double dx = botHead.getX() - playerSegment.getX();
                double dy = botHead.getY() - playerSegment.getY();
                // 만약 거리가 부딪힘 기준변수보다 작으면 bw 반환
                if (dx * dx + dy * dy < WORMWORM) {
                    return bw;    
                }
            }
        }
        return null;	// 아님 그대로 진행
    }
    
    // 봇과 봇이 충돌하는 함수(파라미터 bot 객체)
    public static BotWorm bothitbotbody(ArrayList<BotWorm> bots) {
        for (int i = 0; i < bots.size(); i++) {		// 생성된 BotWorm의 개수만큼 순회
            BotWorm currentBot = bots.get(i);		// 하나의 bot을 가져와 확인할 예정
            Worm.Circle head = currentBot.getHead();	// 가져온 bot의 머리를 가져옴
            if (head == null) {
            	continue;
            }
            
            for (int j = 0; j < bots.size(); j++) {		// 다른 봇을 가져온다
                if (i == j) {	// 혹여나 위의 지렁이와 같은 지렁이가 선택될 수 있으므로 확인
                	continue;
                }
                
                BotWorm otherBot = bots.get(j);		// 다른 봇(index j번째)을 하나 들고옴
                List<Worm.Circle> otherBody = (List<Worm.Circle>) otherBot.getBody();	// 이 봇의 몸통을 가져옴
                
                for (Worm.Circle segment : otherBody) {		// 다른 지렁이의 길이만큼 순회하며 원이 부딪혔는지 확인
                    double dx = head.getX() - segment.getX();	// 머리와 몸통의 x축 y축의 거리 측정
                    double dy = head.getY() - segment.getY();	
                    
                    if (dx * dx + dy * dy < WORMWORM) {		// 거리가 부딪힘기준 상수보다 작으면 bot을 반환
                        return currentBot;    // 충돌 일으킨 봇 객체를 반환
                    }
                }
            }
        }
        return null;	// 아니면 null반환
    }
    
    // BotWorm과 Bullet이 충돌했음을 알려주는 함수
    public static BotWorm wormhitbullet(Bullet bullet, ArrayList<BotWorm> bots) {
        // 생성된 BotWorm의 객체 bots 수만큼 순회
        for (BotWorm bw : bots) {
            List<Worm.Circle> body = (List<Worm.Circle>) bw.getBody();
   
            if (body.size() <= 1) {
            	continue;    
            }
            // head를 제외한 body만큼 순회하고 body의 원과 bullet이 충돌했는지 확인
            for (int i = 1; i < body.size(); i++) {
                Worm.Circle segment = body.get(i);
                
                double dx = bullet.getX() - segment.getX();
                double dy = bullet.getY() - segment.getY();
                
                if (dx*dx + dy*dy < WORMBULLET) {
                    return bw;    
                }
            }
        }
        return null;
    }
    
 // BotWorm과 PlayerWorm의 화산탄 피격확인 메소드(파라미터 화산탄, 플레이어, 봇)
    public static Worm wormhitVolcanoBullet(VolcanoBullet vb, PlayerWorm player, ArrayList<BotWorm> bots) {
        // 화산탄의 x, y좌표 가져옴
        double vx = vb.getIntX();
        double vy = vb.getIntY();
        
        // 충돌 허용 거리 (화산탄 크기 + 지렁이 세그먼트 반지름)의 제곱
        double radiusSq = (vb.getSize() + WORMRADIUS) * (vb.getSize() + WORMRADIUS);    

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