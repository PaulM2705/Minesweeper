package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Minesweepergpt2 extends JFrame {
    private int numRows = 10;
    private int numCols = 10;
    private int numMines = 10;

    private JButton[][] buttons;
    private boolean[][] mines;
    private boolean[][] revealed;

    public Minesweepergpt2() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel();
        JLabel sizeLabel = new JLabel("Size:");
        JTextField sizeField = new JTextField(5);
        JLabel minesLabel = new JLabel("Mines:");
        JTextField minesField = new JTextField(5);
        JButton startButton = new JButton("Start");

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    numRows = Integer.parseInt(sizeField.getText());
                    numCols = numRows;
                    numMines = Integer.parseInt(minesField.getText());
                    initializeGame();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter integers.");
                }
            }
        });

        menuPanel.add(sizeLabel);
        menuPanel.add(sizeField);
        menuPanel.add(minesLabel);
        menuPanel.add(minesField);
        menuPanel.add(startButton);

        add(menuPanel, BorderLayout.NORTH);
        setVisible(true);
    }

    private void initializeGame() {
        getContentPane().removeAll();

        setSize(50 * numCols, 50 * numRows);

        setLayout(new GridLayout(numRows, numCols));

        buttons = new JButton[numRows][numCols];
        mines = new boolean[numRows][numCols];
        revealed = new boolean[numRows][numCols];

        initMines();
        initButtons();

        setVisible(true);
    }

    private void initMines() {
        int count = 0;
        while (count < numMines) {
            int row = (int) (Math.random() * numRows);
            int col = (int) (Math.random() * numCols);
            if (!mines[row][col]) {
                mines[row][col] = true;
                count++;
            }
        }
    }

    private void initButtons() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                JButton button = new JButton();
                final int row = i;
                final int col = j;
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        reveal(row, col);
                    }
                });
                add(button);
                buttons[i][j] = button;
            }
        }
    }

    private void reveal(int row, int col) {
        if (revealed[row][col]) return;

        revealed[row][col] = true;
        buttons[row][col].setEnabled(false);

        if (mines[row][col]) {
            // Game over
            JOptionPane.showMessageDialog(this, "Game Over! You hit a mine!");
            revealAllMines();
        } else {
            // Count adjacent mines
            int count = countAdjacentMines(row, col);
            if (count > 0) {
                buttons[row][col].setText(Integer.toString(count));
            } else {
                // Reveal adjacent cells recursively
                for (int i = Math.max(0, row - 1); i <= Math.min(numRows - 1, row + 1); i++) {
                    for (int j = Math.max(0, col - 1); j <= Math.min(numCols - 1, col + 1); j++) {
                        if (!(i == row && j == col)) {
                            reveal(i, j);
                        }
                    }
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = Math.max(0, row - 1); i <= Math.min(numRows - 1, row + 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(numCols - 1, col + 1); j++) {
                if (mines[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void revealAllMines() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (mines[i][j]) {
                    buttons[i][j].setText("*");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Minesweepergpt2());
    }
}
