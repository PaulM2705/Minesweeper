package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    public Menu() {
        setTitle("Minesweeper Menu");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Minesweeper!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton startButton = new JButton("Press to Play");
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GameBoardMenu();
                dispose();
            }
        });

        panel.add(welcomeLabel, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);
        add(panel);

        setVisible(true);
    }
}