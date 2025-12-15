package Slither;

public class Food {
	// x, y, 크기를 저장하는 상수필드
    private final int x; 
    private final int y; 
    private final int size = 8;
    
    // 생성자(x, y 좌표 저장하는거 말고는 기능 없음)
    public Food(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getter 함수
    public int getX() {
    	return this.x;
    }
    
    public int getY() {
    	return this.y;
    }
    
    public int getSize() {
        return this.size;
    }
}