package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameBoardMenu extends JFrame {

    public GameBoardMenu() {
        setTitle("Game Board Setup");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel setupLabel = new JLabel("Setup your game board", SwingConstants.CENTER);
        setupLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel sizeLabel = new JLabel("Grid Size: ");
        JTextField sizeField = new JTextField(5);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 16));

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

        JPanel sizePanel = new JPanel();
        sizePanel.add(sizeLabel);
        sizePanel.add(sizeField);

        panel.add(sizePanel, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        add(panel);

        setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameBoardMenu();
        });
    }
}
