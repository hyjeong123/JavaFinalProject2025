package Slither;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SettingPanel extends JPanel implements ActionListener {

    private MyFrame frame;
    
    private JLabel title;
    private JPanel settingsPanel;
    private JTextField size;
    private JSlider quantity;
    private JRadioButton firstAbility;
    private JRadioButton secondAbility;
    private JLabel firstText;
    private JLabel secondText;
    private JLabel thirdText;
    private JButton play;
    

    public SettingPanel(MyFrame frame) {
        this.frame = frame;
        
        setLayout(new BorderLayout());

        title = new JLabel("Game Settings", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        JPanel paddingPanel = new JPanel();
        paddingPanel.add(settingsPanel);
        add(paddingPanel, BorderLayout.CENTER);

        firstText = new JLabel("1. Worm Quantity (5 ~ 30)");
        firstText.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(firstText);

        quantity = new JSlider(JSlider.HORIZONTAL, 5, 30, 15);
        quantity.setMajorTickSpacing(5);
        quantity.setMinorTickSpacing(1);
        quantity.setPaintTicks(true);
        quantity.setPaintLabels(true);
        settingsPanel.add(quantity);

        settingsPanel.add(createSpacer(15)); 

        secondText = new JLabel("2. Select your initial size (최소 5)");
        secondText.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(secondText);

        size = new JTextField("10", 10);
        size.setMaximumSize(new Dimension(200, 30));
        settingsPanel.add(size);

        settingsPanel.add(createSpacer(15));

        thirdText = new JLabel("3. Choose your abilities (택 1)");
        thirdText.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        settingsPanel.add(thirdText);

        JPanel abilityPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        ButtonGroup abilityGroup = new ButtonGroup();

        firstAbility = new JRadioButton("Bullet", true); 
        secondAbility = new JRadioButton("Shield");

        abilityGroup.add(firstAbility);
        abilityGroup.add(secondAbility);

        abilityPanel.add(firstAbility);
        abilityPanel.add(secondAbility);
        settingsPanel.add(abilityPanel);

        settingsPanel.add(createSpacer(25));

        play = new JButton("게임 시작!");
        play.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        play.setPreferredSize(new Dimension(200, 50));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(play);
        add(buttonPanel, BorderLayout.SOUTH);

        play.addActionListener(this);
        firstAbility.addActionListener(this);
        secondAbility.addActionListener(this);
        
        // 초기 능력 설정 (기본값: Bullet)
        frame.setBulletQuantity(10);
        frame.setHasShield(false);
    }


    private JPanel createSpacer(int height) {
        JPanel space = new JPanel();
        space.setPreferredSize(new Dimension(10, height));
        return space;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == firstAbility) {
            frame.setBulletQuantity(10);
            frame.setHasShield(false);
        } else if (e.getSource() == secondAbility) {
            frame.setBulletQuantity(0);
            frame.setHasShield(true);
        }
        
        if (e.getSource() == play) {

            int wormQuantity = quantity.getValue();
            int playerSize = 0;

            try {
                playerSize = Integer.parseInt(size.getText().trim()); 
                if (playerSize < 5) {
                    secondText.setText("2. 최소 몸통 길이는 5입니다! (현재: " + playerSize + ")");
                    return;
                }
            } catch (NumberFormatException ex) {
                secondText.setText("2. 숫자를 입력해주세요");
                return;
            }

            frame.setWormQuantity(wormQuantity); 
            frame.setPlayerSize(playerSize);
            
            frame.startGame();
        }
    }
}