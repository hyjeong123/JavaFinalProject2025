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

// JPanel을 상속받고 버튼에 관한 이벤트 사용을 위해 ActionListener을 implement하는 StartPanel
public class StartPanel extends JPanel implements ActionListener {
	// MyFrame, 제목, 버튼을 만들기 위한 참조 선언
    private MyFrame frame;
    private JLabel title;
    private JButton start;
    
    // 이미지 파일 불러오기를 위한 객체	
    private Image backgroundImage;
    // ※ 생성자 영역(MyFrame객체를 파라미터로 받음)
    public StartPanel(MyFrame frame) {
        // frame 객체 생성
    	this.frame = frame;
    	
    	// 동서남북+중앙 관리하는 레이아웃관리자
        setLayout(new BorderLayout());

        // 이미지 불러오는 영역
        try {
        	// 파일을 읽기 위한 클래스 java.io.File에 대한 객체 생성(괄호안은 경로 표시)
            java.io.File backgroundFile = new java.io.File("SlitherBackground/SlitherStartPanel.jpeg");
            // 만일 파일이 있다면 파일을 읽어들이고 backgroudImage에 읽어온다 
            if (backgroundFile.exists()) {
                backgroundImage = ImageIO.read(backgroundFile);
            } else {
                throw new java.io.IOException("니 파일 위치: " + backgroundFile.getAbsolutePath());
            }	// 아니라면 예외처리에 떤져버리고 실제 파일이 있는 위치를 보여줘라
        } catch (java.io.IOException e) {	
            e.printStackTrace();	// 오류 내용 출력
            setBackground(Color.LIGHT_GRAY);
        }	// 만약 진짜 exception이 발생한다면 오류 내용 출력하고 배경을 그냥 연한 회색으로 바꾼다
        
        // 제목 레이블을 CENTER에 놓기 위해 SwingConstants를 이용하여 중앙에 배치함(swing패키지 안에 있음)
        title = new JLabel("지렁이 게임", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 48));
        title.setForeground(Color.YELLOW); // 배경이 어두울 경우 글자색 변경
        
        // 시작버튼 설정하기
        start = new JButton("Play");
        start.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        start.setPreferredSize(new Dimension(250, 50));
        
        // 시작버튼을 담을 버튼패널 생성(왜 패널 만드냐 --> 버튼은 바로 swingconstants 못하기 때문임 ㅋ
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // 버튼 패널 투명하게 설정
        buttonPanel.add(start);

        add(title, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        start.addActionListener(this);    
    }

    // paintComponent 영역
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);	// 지우개
        if (backgroundImage != null) {
            // 패널 크기(getWidth(), getHeight())에 맞춰 이미지를 그립니다. (img, x, y, w, h, observer)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
    // 버튼의 actionEvent 영역
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {	// 버튼 누를시
            frame.showPanel("Setting"); // frame객체의 showPanel함수를 호출해 SettingPanel로 넘어감
        }
    }
}