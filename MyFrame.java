package Slither;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MyFrame extends JFrame {
    private CardLayout layout;
    private JPanel cardPanel;

    // SettingPanel에서 받은 값 저장
    private int wormquantity;
    private int playersize;

    public MyFrame() {
        layout = new CardLayout();
        cardPanel = new JPanel(layout);

        StartPanel startpanel = new StartPanel(this);
        SettingPanel settingpanel = new SettingPanel(this);

        cardPanel.add(startpanel, "Start");
        cardPanel.add(settingpanel, "Setting");

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

    public int getquantity() {
        return wormquantity;
    }
    public void setquantity(int quantity) {
        wormquantity = quantity;
    }

    public int getsize() {
        return playersize;
    }
    public void setsize(int size) {
        playersize = size;
    }

    // 게임 시작 버튼 클릭 시 호출
    public void startGame() {
        GamePanel gamepanel = new GamePanel(this); // 새로 생성
        cardPanel.add(gamepanel, "Game");          // 카드에 추가
        showPanel("Game");                         // 전환
    }
}
