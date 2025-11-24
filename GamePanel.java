package Slither;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements KeyListener, MouseMotionListener {

    private MyFrame frame;
    
    // player 객체 생성, 가변적배열 bots 객체 생성
    private PlayerWorm player;
    private ArrayList<BotWorm> bots = new ArrayList<>();
    
    // player 지렁이 크기, 싸울 봇 지렁이 개수
    private int bodysize;
    private int quantity;
    
    // player 시작점 (x, y)
    private int mouseX = 400;
    private int mouseY = 300;
    
    // timer객체
    private Timer timer;
    
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Worm> deadWorms = new ArrayList<>();

    // GamePanel 생성자
    public GamePanel(MyFrame frame) {
        this.frame = frame;

        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);

        // SettingPanel에서 입력한 값 사용
        bodysize = frame.getsize();
        quantity = frame.getquantity();

        player = new PlayerWorm(400, 300, bodysize);

        bots = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            int botX = (int)(Math.random() * 800);
            int botY = (int)(Math.random() * 600);
            int botsize = (int)(Math.random() * 80) + 20;
            bots.add(new BotWorm(botX, botY, botsize));
        }

        timer = new Timer(16, e -> {
            player.move(mouseX, mouseY);
            repaint();
        });
        timer.start();

        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }



    @Override public void keyTyped(KeyEvent e) { }
    @Override public void keyReleased(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) {
            bullets.add(new Bullet(player.x, player.y, mouseX, mouseY));
        }
    }

    @Override public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경색
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        // 플레이어 몸
        for (int i = 0; i < player.body.size(); i++) {
            Worm.Circle c = player.body.get(i);
            g.setColor(Color.GREEN);
            g.fillOval(c.x - 6, c.y - 6, 12, 12);
        }

        // 머리 강조
        g.setColor(Color.WHITE);
        Worm.Circle head = player.body.get(0);
        g.drawOval(head.x - 7, head.y - 7, 14, 14);

        // 총알
        g.setColor(Color.YELLOW);
        for (Bullet b : bullets) {
            g.fillOval(b.x - 3, b.y - 3, 6, 6);
        }
    }
}
