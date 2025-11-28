package Slither;

public class PlayerWorm extends Worm {
	public PlayerWorm(int x, int y, int size, int speed) {
        super(x, y, size, speed);
    }
	public void setplayerspeed(int speed) {
	    this.speed = speed;		// this.speed는 Worm클래스에 선언되어 상속된것
	}
	
}
