package Slither;

import java.awt.*;
import javax.swing.*;

// JFrame을 상속받는 MyFrame
public class MyFrame extends JFrame {
    private CardLayout layout;	// 화면에 보이는 4개의 패널들을 담을 CardLayout형 layout
    private JPanel cardPanel;	// CardLayout으로 화면전환을 보여줄 4개의 패널을 담을 JPanel cardPanel

    // 각 패널별 객체 생성
    private StartPanel startPanel;
    private SettingPanel settingPanel;
    private FinPanel finPanel;
    private GamePanel gamepanel; 

    // 설정 값 필드 (봇 지렁이 개수, 플레이어 길이, 플레이어 이동속도, 총알 개수, 방패막 생성되었는지 확인)
    private int wormquantity;
    private int playersize;
    private int playerspeed = 5; // 기본 속도 5로 지정
    private int bulletquantity;
    private boolean shieldpick = false; // 라디오 버튼이 선택되지 않은것이 기본이라 false

    // ※생성자 영역
    public MyFrame() {
    	// 카드 레이아웃을 객체 생성하여 cardPanel을 생성한다, cardPanel은 layout을 적용하여 담는다
        layout = new CardLayout();
        cardPanel = new JPanel(layout);
        
        // 나중에 frame. Panel할때 생성중인 그 MyFrame 객체 자체에 참조를 줄 수 있도록 this를함
        startPanel = new StartPanel(this);
        settingPanel = new SettingPanel(this);
        finPanel = new FinPanel(this);
        
        // cardPanel이란 JPanel에 gamePanel을 제외하고 추가
        cardPanel.add(startPanel, "Start");
        cardPanel.add(settingPanel, "Setting");
        cardPanel.add(finPanel, "Fin");
        
        // MyFrame에 3개의 panel을 담은 cardPanel을 추가
        add(cardPanel);
        
        // 별거 없음 setLocationRelaticeTo(null)은 창을 가운데 띄워준다
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("지렁이게임");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 화면 전환을 관리하는 메소드 showPanel(파라미터 cardPanel의 이름)
    public void showPanel(String name) {
        	layout.show(cardPanel, name);	// CardLayout 객체 layout의 show함수 부름(show는 패키지 내의 함수), cardPanel내의 Panel이름 입력
    	}

    // 설정 값 Getter/Setter함수(봇 지렁이, 플레이어 지렁이 길이, 플레이어 이동속도, 총알 개수, 방어막 선택)
    public int getWormQuantity() { 
    		return wormquantity; 
    	}
    public void setWormQuantity(int quantity) { 
    		this.wormquantity = quantity; 
    	}

    public int getPlayerSize() { 
    		return playersize; 
    	}
    public void setPlayerSize(int size) { 
    		this.playersize = size; 
    	}
    
    public int getPlayerSpeed() { 
    		return playerspeed; 
    	}
    public void setPlayerSpeed(int speed) { 
    		this.playerspeed = speed; 
    	} 
    
    public int getBulletQuantity() { 
    		return bulletquantity; 
    	}
    public void setBulletQuantity(int quantity) { 
    		this.bulletquantity = quantity; 
    	}
    
    public boolean getHasShield() { 
    		return shieldpick; 
    	}
    public void setHasShield(boolean shieldpick) { 
    		this.shieldpick = shieldpick; 
    	}

    // 게임 시작시의 작동을 구현하는 함수 startGame()
    public void startGame() {
    	// 이전의 게임이 실행중이라면
        if (gamepanel != null) {
            gamepanel.gameStop();			// gamepanel내의 gameStop()함수를 호출하여 게임 정지 		
            cardPanel.remove(gamepanel);	// cardPanel에서 기존의 gamepanel을 제거한다
        }
        
        gamepanel = new GamePanel(this);	// 새 GamePanel의 객체를 생성하고 cardPanel에 추가한다
        cardPanel.add(gamepanel, "Game");	
        
        showPanel("Game");					// 그리고 실행한다
    }
    
    // 게임 종료 되었을때의 작동을 구현하는 함수 gameOver()
    public void gameOver(int finalScore) {
    	// finPanel의 최종점수를 설정한다
        finPanel.setFinalScore(finalScore);	
        showPanel("Fin");	// FinPanel로 전환하라
    }
}