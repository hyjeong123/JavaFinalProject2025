package Slither;

import java.awt.event.ActionListener;

import javax.swing.Timer;

public class BotWorm extends Worm {
    public BotWorm(int x, int y, int size, int speed) {
        super(x, y, size, speed);
    }
    
    public void move(int targetX, int targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist != 0) {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }

        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).x = body.get(i - 1).x;
            body.get(i).y = body.get(i - 1).y;
        }

        body.get(0).x = x;
        body.get(0).y = y;
    }

}
