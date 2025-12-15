package Slither;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory; // BorderFactory 추가
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FinPanel extends JPanel implements ActionListener {
    
    private MyFrame frame;
    private JLabel title; 
    private JLabel score;
    private JButton gostart;
    
    public FinPanel(MyFrame frame) {
        this.frame = frame;
        
        // 1. 메인 패널 디자인 및 색상 설정
        setBackground(Color.DARK_GRAY); // 어두운 배경색
        // BoxLayout을 사용하되, 중앙에 배치하기 위해 Y_AXIS 대신 X_AXIS 정렬 패널 사용
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
        
        // 2. 상단 여백 추가 (제목을 중앙으로 내리기 위해)
        add(Box.createVerticalStrut(100)); // 높이 100 픽셀의 여백 추가
        
        // 3. GAME OVER 제목
        title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 60)); // 폰트 크기 강조
        title.setForeground(Color.RED); // 빨간색으로 시각적 강조
        title.setAlignmentX(CENTER_ALIGNMENT); 	// setAlignmentX는 컴포넌트를 x축(수평)으로 정렬시키라는 뜻
        add(title);
        
        // 4. 제목과 점수 사이에 여백 추가
        add(Box.createVerticalStrut(30)); // 높이 30 픽셀의 여백 추가
        
        // 5. 점수 레이블
        score = new JLabel("최종 점수: 0 점", SwingConstants.CENTER); // 초기 텍스트 수정
        score.setFont(new Font("맑은 고딕", Font.BOLD, 36)); // 점수 폰트 크기 키우기
        score.setForeground(Color.WHITE); // 흰색 글씨
        score.setAlignmentX(CENTER_ALIGNMENT);
        add(score);
        
        // 6. 점수와 버튼 사이에 여백 추가
        add(Box.createVerticalStrut(50)); // 높이 50 픽셀의 여백 추가
        
        // 7. '처음으로' 버튼
        gostart = new JButton("처음으로 돌아가기"); // 텍스트 수정
        gostart.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        gostart.setPreferredSize(new Dimension(250, 60));
        gostart.setAlignmentX(CENTER_ALIGNMENT);
        
        // 버튼 주변에 여백을 주기 위한 빈 Border 추가 (깔끔하게 보임)
        gostart.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        
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