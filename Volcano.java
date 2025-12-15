package Slither;

// 이것도 그냥 Food클래스랑 다를바가 없다
public class Volcano {
    private final int x;
    private final int y;
    private final int size = 50; 
    
    public Volcano(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
}