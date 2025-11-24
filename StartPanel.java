package Slither;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartPanel extends JPanel implements ActionListener {

    private MyFrame frame; 
    private JPanel title;
    private JLabel name;
    private JButton start;

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
}
