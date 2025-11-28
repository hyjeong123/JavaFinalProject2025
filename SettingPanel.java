package Slither;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.BoxLayout;

public class SettingPanel extends JPanel implements ActionListener {

    private MyFrame frame; 
    
    public JLabel title;
    private JPanel settingsPanel; // 전체 설정 묶음 패널
    public JTextField size;
    public JSlider quantity;
    public JRadioButton firstability;
    public JRadioButton secondability;
    public JRadioButton thirdability;
    public JLabel firsttext;
    public JLabel secondtext;
    public JLabel thirdtext;
    public JButton play;
    

    public SettingPanel(MyFrame frame) {
        this.frame = frame; 
        
        // =============================
        // 📌 [배치 영역 설정]
        // =============================
        setLayout(new BorderLayout());

        // ----------------------------
        // 1. 상단 제목
        // ----------------------------
        title = new JLabel("Game Settings", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        // ----------------------------
        // 2. 중앙 설정 패널 (세로 정렬)
        // ----------------------------
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        // (공통 여백 패널)
        JPanel paddingPanel = new JPanel();
        paddingPanel.add(settingsPanel);
        add(paddingPanel, BorderLayout.CENTER);


        // =============================
        // 📌 [설정 항목 구성]
        // =============================

        // ---- (1) 지렁이 수량 ----
        firsttext = new JLabel("1. Worm Quantity (5 ~ 30)");
        firsttext.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(firsttext);

        quantity = new JSlider(JSlider.HORIZONTAL, 5, 30, 15);
        quantity.setMajorTickSpacing(5);
        quantity.setMinorTickSpacing(1);
        quantity.setPaintTicks(true);
        quantity.setPaintLabels(true);
        settingsPanel.add(quantity);

        settingsPanel.add(createSpacer(15)); // 간격


        // ---- (2) 플레이어 크기 ----
        secondtext = new JLabel("2. Select your initial size (숫자 입력)");
        secondtext.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(secondtext);

        size = new JTextField("", 10);
        size.setMaximumSize(new Dimension(200, 30));
        settingsPanel.add(size);

        settingsPanel.add(createSpacer(15));


        // ---- (3) 능력 선택 ----
        thirdtext = new JLabel("3. Choose your abilities (택 1)");
        thirdtext.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(thirdtext);

        JPanel abilityPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        ButtonGroup abilityGroup = new ButtonGroup();

        firstability = new JRadioButton("Bullet");
        secondability = new JRadioButton("Shield");

        abilityGroup.add(firstability);
        abilityGroup.add(secondability);

        abilityPanel.add(firstability);
        abilityPanel.add(secondability);
        settingsPanel.add(abilityPanel);

        settingsPanel.add(createSpacer(25));


        // =============================
        // 📌 [게임 시작 버튼]
        // =============================
        play = new JButton("게임 시작!");
        play.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        play.setPreferredSize(new Dimension(200, 50));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(play);
        add(buttonPanel, BorderLayout.SOUTH);

        play.addActionListener(this);
    }



    // =============================
    // 📌 [간격 생성 함수]
    // =============================
    private JPanel createSpacer(int height) {
        JPanel space = new JPanel();
        space.setPreferredSize(new Dimension(10, height));
        return space;
    }



    // =============================
    // 📌 [버튼 클릭 이벤트]
    // =============================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == firstability) {
        	frame.bulletquantity(10);
        }
    	
    	if (e.getSource() == play) {

            int wormquantity = quantity.getValue();
            int playersize = 0;

            try {
                playersize = Integer.parseInt(size.getText());
                if (playersize < 5) {
                    secondtext.setText("최소 몸통 길이는 5입니다!");
                    return;
                }
            } catch (NumberFormatException ex) {
                secondtext.setText("숫자를 입력해주세요");
                return;
            }

            frame.setquantity(wormquantity);
            frame.setsize(playersize);

            // 게임 시작
            frame.startGame();
        }
    }
}
