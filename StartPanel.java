package Slither;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartPanel extends JPanel implements ActionListener {

    private MyFrame frame; 
    private JPanel title;
    private JLabel name;
    private JButton start;
    private BufferedImage smileworm;
    public StartPanel(MyFrame frame) {
        this.frame = frame;
        
        title = new JPanel();
        add(title);
        
        name = new JLabel("지렁이 게임");
        add(name);
        
        start = new JButton("플레이 하기");
        add(start);

        start.addActionListener(this);  
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            frame.showPanel("Setting");  
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	g.drawImage(smileworm, 0, 0, 650, 650, null);
    }
}
