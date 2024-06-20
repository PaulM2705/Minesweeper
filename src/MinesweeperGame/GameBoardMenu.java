package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameBoardMenu extends JFrame {

    public GameBoardMenu() {
        setTitle("Game Board Setup");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon backgroundImage = new ImageIcon(GameBoardMenu.class.getResource("Minesweeper.jpeg"));

        JPanel panelBack = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelBack.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel setupLabel = new JLabel("Setup your game board", SwingConstants.CENTER);
        setupLabel.setFont(new Font("Serif", Font.ITALIC, 50));
        setupLabel.setForeground(Color.BLACK);

        JLabel sizeLabel = new JLabel("Grid Size: ");
        sizeLabel.setFont(new Font("Serif", Font.ITALIC, 25));
        JTextField sizeField = new JTextField(10);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Serif", Font.ITALIC, 30));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int gridSize = Integer.parseInt(sizeField.getText());
                    if (gridSize <= 0) {
                        throw new IllegalArgumentException("Grid size must be greater than 0.");
                    }
                    JFrame frame = new JFrame("Minesweeper");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    GameBoard gameBoard = new GameBoard(gridSize, new ScoreManager(frame));
                    frame.getContentPane().add(gameBoard);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number for grid size.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(setupLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(sizeLabel);
        inputPanel.add(sizeField);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        panelBack.add(panel, BorderLayout.CENTER);

        getContentPane().add(panelBack);

        setVisible(true);
    }
}
