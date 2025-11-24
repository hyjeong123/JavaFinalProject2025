package Slither;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FinPanel extends JPanel implements ActionListener {
	private MyFrame frame;
	private JLabel score;
	private JButton gostart;
	public FinPanel(MyFrame frame) {
		score = new JLabel("Your score");
		add(score);
		gostart = new JButton("처음으로");
		add(gostart);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == gostart) {
			frame.showPanel("Start");
		}
	}
}
