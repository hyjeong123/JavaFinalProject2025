package Slither;

public class Food {

    private final int x; 
    private final int y; 
    private final int size = 8;
    
    // 생성자
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