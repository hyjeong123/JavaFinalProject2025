package Slither;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StartPanel extends JPanel implements ActionListener {

    private MyFrame frame;
    private JLabel name;
    private JButton start;

    public StartPanel(MyFrame frame) {
        this.frame = frame;
        
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        name = new JLabel("지렁이 게임", SwingConstants.CENTER);
        name.setFont(new Font("맑은 고딕", Font.BOLD, 48));
        
        start = new JButton("플레이 하기");
        start.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        start.setPreferredSize(new Dimension(250, 60));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(start);

        add(name, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        start.addActionListener(this);	
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            frame.showPanel("Setting");
        }
    }
}