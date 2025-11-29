package Slither;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;

public class FinPanel extends JPanel implements ActionListener {
    
    private MyFrame frame;
    private JLabel score;
    private JButton gostart;
    
    public FinPanel(MyFrame frame) {
        this.frame = frame;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        score = new JLabel("게임 종료", SwingConstants.CENTER); 
        score.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        score.setAlignmentX(CENTER_ALIGNMENT); 
        add(score);
        
        gostart = new JButton("처음으로");
        gostart.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        gostart.setAlignmentX(CENTER_ALIGNMENT); 
        
        add(gostart);
        
        gostart.addActionListener(this);
    }
    
    public void setFinalScore(int finalScore) {
        score.setText("최종 점수: " + finalScore + " 점");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == gostart) {
            frame.showPanel("Start");
        }
    }
}