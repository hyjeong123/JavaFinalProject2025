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

public class SettingPanel extends JPanel implements ActionListener {

    private MyFrame frame; 
    
    public JLabel title;
    private JPanel settings;
    public JTextField size;
    public JSlider quantity;
    public JRadioButton firstability;
    public JRadioButton secondability;
    public JRadioButton thirdability;
    public JLabel firsttext;
    public JLabel secondtext;
    public JLabel thirdtext;
    public JButton play;
    // 여기서 panel끼리 이동하며 사용될 변수는 size(내 지렁이 크기), quantity(봇 지렁이 개수), firstability, secondability, thirdability <= 능력 세개
    public SettingPanel(MyFrame frame) {
        this.frame = frame; 
        
        setLayout(new BorderLayout()); 

        title = new JLabel("Game Settings");
        add(title, BorderLayout.NORTH);

        settings = new JPanel();
        
        // --- 1. 벌레 수량 (Worm quantity) 설정 ---
        firsttext = new JLabel("1. Worm quantity (5 ~ 30)");
        settings.add(firsttext);

        quantity = new JSlider(JSlider.HORIZONTAL, 5, 30, 15);
        quantity.setMajorTickSpacing(5);	// 눈금 사이의 거리
        quantity.setMinorTickSpacing(1);	// 눈금 사이의 크기
        quantity.setPaintTicks(true);		// 슬라이더 눈금 표시
        quantity.setPaintLabels(true);		// 슬라이더 레이블 값 표시
        settings.add(quantity);

        // --- 2. 뱀 크기 (Select your size) 설정 ---
        secondtext = new JLabel("2. Select your initial size (숫자 입력)");
        secondtext.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settings.add(secondtext);

        size = new JTextField("", 10);
        size.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        settings.add(size);

        // --- 3. 능력 선택 (Choose your abilities) 설정 ---
        thirdtext = new JLabel("3. Choose your abilities (택 1)");
        thirdtext.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settings.add(thirdtext);
        
        // 라디오 버튼을 가로로 정렬하고 그룹화하기 위한 패널 및 그룹 생성
        JPanel abilityPanel = new JPanel(new GridLayout(1, 3, 10, 0)); // 1행 3열, 가로 간격 10
        ButtonGroup abilityGroup = new ButtonGroup();

        firstability = new JRadioButton("Ability 1: Speed Boost");
        secondability = new JRadioButton("Ability 2: Shield");
        thirdability = new JRadioButton("Ability 3: Teleport");
        
        // 버튼 그룹에 추가 (하나만 선택 가능하게)
        abilityGroup.add(firstability);
        abilityGroup.add(secondability);
        abilityGroup.add(thirdability);
        secondability.setSelected(true); // 기본값 설정

        // 라디오 버튼 패널에 추가
        abilityPanel.add(firstability);
        abilityPanel.add(secondability);
        abilityPanel.add(thirdability);

        settings.add(abilityPanel); 

        // 플레이 버튼 설정
        play = new JButton("게임 시작!");
        play.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        play.setPreferredSize(new Dimension(200, 50));

        JPanel wrapperPanel = new JPanel(); 
        wrapperPanel.add(settings);
        
        add(wrapperPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(); // 버튼 중앙 정렬을 위한 패널
        southPanel.add(play);
        add(southPanel, BorderLayout.SOUTH); 
        
        play.addActionListener(this); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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

            // ★ 게임 시작 시점에 GamePanel 생성
            frame.startGame();
        }
    }

}