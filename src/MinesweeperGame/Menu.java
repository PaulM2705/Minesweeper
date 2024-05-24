package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
	
	public Menu () {
		
		setTitle("Willkommen bein Minesweeper");
		setSize(400,200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JLabel welcomeLabel = new JLabel("Willkommen bei Minesweeper" , SwingConstants.CENTER);
		welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
		
		JButton startButton = new JButton("Press to Play");
		startButton.setFont(new Font("Arial", Font.PLAIN,16));
		
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		
		panel.add(welcomeLabel, BorderLayout.CENTER);
		panel.add(startButton,BorderLayout.SOUTH);
		add(panel);
		
	}
	
	private void startGame() {
		dispose();
		
		MinesweeperMain game = new MinesweeperMain();
		game.start();
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Menu menu = new Menu();
				menu.setVisible(true);
			}
		});
	}
}