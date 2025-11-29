package Slither;

public class BotWorm extends Worm {
    public BotWorm(int x, int y, int size, int speed) {
        super(x, y, size, speed);
    }
    
    // Worm의 move(int targetX, int targetY) 메서드를 상속받아 사용합니다.
    // 봇 AI 로직이 추가될 때만 오버라이드하면 됩니다.
}