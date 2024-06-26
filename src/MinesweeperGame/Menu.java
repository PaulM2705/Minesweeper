package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    public Menu() {
        setTitle("Minesweeper Menu");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon backgroundImage = new ImageIcon(Menu.class.getResource("Minesweeper.jpeg"));

        JPanel panelBack = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelBack.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Minesweeper!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.ITALIC, 50));
        welcomeLabel.setForeground(Color.BLACK);

        JButton startButton = new JButton("Press to Play");
        startButton.setFont(new Font("Serif", Font.ITALIC, 30));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GameBoardMenu();
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);

        buttonPanel.add(startButton, BorderLayout.CENTER);

        contentPanel.add(welcomeLabel, BorderLayout.NORTH);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)), BorderLayout.SOUTH);

        panelBack.add(contentPanel, BorderLayout.CENTER);
        panelBack.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(panelBack);

        setVisible(true);
    }


};
