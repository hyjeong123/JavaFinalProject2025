package Slither;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// JPanel을 상속받고 ActionListener을 구현하는 SettingPanel 클래스	
public class SettingPanel extends JPanel implements ActionListener {

    private MyFrame frame;	// 부모역할을 하는 MyFrame 객체를 참조하는 필드
    
    // 각 컴포넌트 생성
    private JLabel title;	// Game Settings라는 제목을 보여주는 레이블
    private JPanel settingsPanel;	// JTextField, JSlider를 담는 panel(BorderLayout때문에 사용)
    private JPanel bagPanel;
    private JTextField size;
    private JSlider quantity;
    private JRadioButton firstAbility;
    private JRadioButton secondAbility;
    private JLabel firstText;
    private JLabel secondText;
    private JLabel thirdText;
    private JButton play;
    
    // 생성자 영역(파라미터 부모 프레임의 인스턴스 frame)
    public SettingPanel(MyFrame frame) {
        this.frame = frame;
      
        setLayout(new BorderLayout());	// BorderLayout설정
        
        // 제목 설정(북쪽에 위치)
        title = new JLabel("Game Settings", JLabel.CENTER);	
        title.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);
        
        // 설정하는데 쓰는 컴포넌트를 담을 settingsPanel 패널 생성. Y_AXIS는 컴포넌트들을 세로 축으로 쌓는다(아래에서 위로)
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        
        // 세로 축으로 쌓은 컴포넌트를 중앙에만 정렬하기 위해 새로운 JPanel을 생성한다
        bagPanel = new JPanel();
        bagPanel.add(settingsPanel);
        add(bagPanel, BorderLayout.CENTER);
        
        // 첫번째 설정, 지렁이 개수 설정에 대한 제목
        firstText = new JLabel("1. AI 지렁이 개수 설정(5개 - 30개)");
        firstText.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(firstText);
        
        // 지렁이 개수 설정에 대한 JSlider
        quantity = new JSlider(JSlider.HORIZONTAL, 5, 30, 5);	// 수평방향으로 최소값 5, 최대값 30, 최초값 5로 맞춤
        quantity.setMajorTickSpacing(5);	// 큰 눈금 간격을 5로 설정함
        quantity.setMinorTickSpacing(1);	// 작은 눈금 간격을 1로 설정함
        // 눈금과 숫자레이블을 보이게 함(true)
        quantity.setPaintTicks(true);		
        quantity.setPaintLabels(true);
        settingsPanel.add(quantity);
        
        // SettingPanel에 여백을 추가하는 함수 createSpace를 추가(간격 15)
        settingsPanel.add(createSpace(15));	
        
        // 두번째 설정, 지렁이 몸 길이 설정에 대한 제목
        secondText = new JLabel("2. 몸 길이 설정(최소 5, 최대 15)");
        secondText.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(secondText);
        
        // 몸 길이를 받아노는 JTextField 설정 기본 값 10으로 설정
        size = new JTextField();				// 초기 값 10이랑 너비 10으로 설정
        size.setMaximumSize(new Dimension(200, 30));	// TextField의 크기를 200 by 30으로 제한
        settingsPanel.add(size);
        
        // 능력사이의 간격 15로 설정
        settingsPanel.add(createSpace(15));
        
        // 세번째 설정, 플레이어 지렁이의 능력 하나 선택
        thirdText = new JLabel("3. 능력을 선택하시오 (택 1)");
        thirdText.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(thirdText);
        
        // 능력 두개를 넣을 abilityPanel을 만든다, 컴포넌트 가로 간격은 10이다
        JPanel abilityPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        // ButtonGroup을 사용하여 능력 두 개중 하나만 사용 가능하도록 만든다
        ButtonGroup abilityGroup = new ButtonGroup();
        
        // Bullet, Shield 능력을 라디오 버튼으로 만든다
        firstAbility = new JRadioButton("총알 발사");	
        secondAbility = new JRadioButton("방어막");
        
        // abilityGroup에 두 버튼을 넣음(하나만 택하기 위함)
        abilityGroup.add(firstAbility);
        abilityGroup.add(secondAbility);
        // abilityPanel에 두 버튼을 넣어서 능력 그룹을 만든다
        abilityPanel.add(firstAbility);
        abilityPanel.add(secondAbility);
        settingsPanel.add(abilityPanel);
        
        // 간격 띄운다
        settingsPanel.add(createSpace(25));
        
        // 시작하기 버튼 만든다
        play = new JButton("게임 시작!");
        play.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        play.setPreferredSize(new Dimension(200, 50));	// 선호하는 버튼 크기로 설정한다 (200 x 50)
        // play버튼을 아래에 두기 위해 JPanel을 만들어서 넣는다
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(play);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 각 버튼들에 대한 ActionListener을 둔다
        play.addActionListener(this);
        firstAbility.addActionListener(this);
        secondAbility.addActionListener(this);
        
        // 초기 능력 설정 (기본값: Bullet, 방어막 선택안됨), frame객체의 함수 가져옴
        frame.setBulletQuantity(10);
        frame.setHasShield(false);
    }

    // 아까 생성자 내에서 간격띄우려고 만들어놓은 함수
    private JPanel createSpace(int height) {
        JPanel space = new JPanel();
        space.setPreferredSize(new Dimension(10, height));
        return space;
    }

    
    // ActionEvent 영역
    @Override
    public void actionPerformed(ActionEvent e) {
    	// 선택된 능력에 따라 총알개수와 방어막 선택여부를 한다
        if (e.getSource() == firstAbility) {
            frame.setBulletQuantity(10);
            frame.setHasShield(false);
        } else if (e.getSource() == secondAbility) {
            frame.setBulletQuantity(0);
            frame.setHasShield(true);
        }
        // play버튼 누를시에 발동되는 영역
        if (e.getSource() == play) {

            int wormQuantity = quantity.getValue();
            int playerSize = 0;
            // player몸길이가 너무 짧거나 숫자 말고 다른 걸 입력했을 시에 쓰는 try - catch문 
            try {
                playerSize = Integer.parseInt(size.getText().trim());	
                if (playerSize < 5) {
                    secondText.setText("2. 최소 몸통 길이는 5입니다! (현재: " + playerSize + ")");
                    return;
                }
                else if (playerSize > 15) {
                    secondText.setText("2. 최대 몸통 길이는 15입니다! (현재: " + playerSize + ")");
                    return;
                }
            } catch (NumberFormatException ex) {
                secondText.setText("2. 숫자를 입력해주세요");
                return;
            }
            
            // MyFrame객체의 지렁이 개수와 플레이어 몸길이를 설정한다
            frame.setWormQuantity(wormQuantity);	
            frame.setPlayerSize(playerSize);
            
            // frame에서 설정한 startGame함수 가져와 실행
            frame.startGame();
        }
    }
}