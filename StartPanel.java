package Slither;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics; // Graphics 객체 임포트
import java.awt.Image;    // Image 객체 임포트
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException; // 예외 처리 임포트
import java.io.InputStream;

import javax.imageio.ImageIO; // 이미지 로딩 임포트
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StartPanel extends JPanel implements ActionListener {

    private MyFrame frame;
    private JLabel name;
    private JButton start;
    
    // 1. 이미지 필드 추가
    private Image backgroundImage;

    public StartPanel(MyFrame frame) {
        this.frame = frame;
        
        setLayout(new BorderLayout());

     // StartPanel.java 내의 이미지 로드 부분
        try {
            // 💡 수정된 경로: 현재 작업 디렉토리 (Mainprtc) 바로 아래에 SlitherBackground가 있다고 가정
            java.io.File imageFile = new java.io.File("SlitherBackground/SlitherStartPanel.jpeg");
            
            System.out.println("DEBUG: Absolute Path = " + imageFile.getAbsolutePath()); 
            
            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
            } else {
                throw new java.io.IOException("파일을 찾을 수 없습니다: " + imageFile.getAbsolutePath());
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            setBackground(Color.LIGHT_GRAY);
        }
        name = new JLabel("지렁이 게임", SwingConstants.CENTER);
        name.setFont(new Font("맑은 고딕", Font.BOLD, 48));
        name.setForeground(Color.WHITE); // 배경이 어두울 경우 글자색 변경

        start = new JButton("플레이 하기");
        start.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        start.setPreferredSize(new Dimension(250, 60));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // 버튼 패널 투명하게 설정
        buttonPanel.add(start);

        add(name, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        start.addActionListener(this);    
    }

    // 3. paintComponent 오버라이드
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // 패널 크기(getWidth(), getHeight())에 맞춰 이미지를 그립니다.
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            frame.showPanel("Setting");
        }
    }
}