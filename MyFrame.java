package Slither;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MyFrame extends JFrame {
    private CardLayout layout;
    private JPanel cardPanel;

    private StartPanel startPanel;
    private SettingPanel settingPanel;
    private FinPanel finPanel;
    private GamePanel currentGamePanel; // 현재 실행 중인 GamePanel 인스턴스

    // 설정 값 필드
    private int wormQuantity;
    private int playerSize;
    private int playerSpeed = 5; // 기본 속도
    private int bulletQuantity;
    private boolean hasShield = false; 	

    public MyFrame() {
        layout = new CardLayout();
        cardPanel = new JPanel(layout);

        startPanel = new StartPanel(this);
        settingPanel = new SettingPanel(this);
        finPanel = new FinPanel(this);
        
        cardPanel.add(startPanel, "Start");
        cardPanel.add(settingPanel, "Setting");
        cardPanel.add(finPanel, "Fin");
        
        add(cardPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("지렁이게임");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showPanel(String name) {
        layout.show(cardPanel, name);
    }

    // 설정 값 Getter/Setter
    public int getWormQuantity() { return wormQuantity; }
    public void setWormQuantity(int quantity) { this.wormQuantity = quantity; }

    public int getPlayerSize() { return playerSize; }
    public void setPlayerSize(int size) { this.playerSize = size; }
    
    public int getPlayerSpeed() { return playerSpeed; }
    public void setPlayerSpeed(int speed) { this.playerSpeed = speed; } // 현재는 사용 안 함
    
    public int getBulletQuantity() { return bulletQuantity; }
    public void setBulletQuantity(int quantity) { this.bulletQuantity = quantity; }
    
    public boolean getHasShield() { return hasShield; }
    public void setHasShield(boolean hasShield) { this.hasShield = hasShield; }

    // 게임 시작 (메모리 관리 로직 포함)
    public void startGame() {
        // 기존 게임 패널 제거 (메모리 누수 방지)
        if (currentGamePanel != null) {
            cardPanel.remove(currentGamePanel);
        }
        
        currentGamePanel = new GamePanel(this); 
        cardPanel.add(currentGamePanel, "Game"); 
        
        showPanel("Game");
    }
    
    // 게임 종료 및 FinPanel 전환
    public void gameOver(int finalScore) {
        finPanel.setFinalScore(finalScore); 
        showPanel("Fin");
    }
}