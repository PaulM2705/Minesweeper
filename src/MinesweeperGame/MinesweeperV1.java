package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MinesweeperV1 extends JFrame {
    private final int ROWS = 10;
    private final int COLS = 10;
    private final int NUM_MINES = 10;

    private JButton[][] buttons;
    private boolean[][] mines;
    private boolean[][] revealed;

    public MinesweeperV1() {
        setTitle("Minesweeper");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(ROWS, COLS));
        
        buttons = new JButton[ROWS][COLS];
        mines = new boolean[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        
        initMines();
        initButtons();
        
        setVisible(true);
    }
    
    private void initMines() {
        int count = 0;
        while (count < NUM_MINES) {
            int row = (int) (Math.random() * ROWS);
            int col = (int) (Math.random() * COLS);
            if (!mines[row][col]) {
                mines[row][col] = true;
                count++;
            }
        }
    }
    
    private void initButtons() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
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
                for (int i = Math.max(0, row - 1); i <= Math.min(ROWS - 1, row + 1); i++) {
                    for (int j = Math.max(0, col - 1); j <= Math.min(COLS - 1, col + 1); j++) {
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
        for (int i = Math.max(0, row - 1); i <= Math.min(ROWS - 1, row + 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(COLS - 1, col + 1); j++) {
                if (mines[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private void revealAllMines() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (mines[i][j]) {
                    buttons[i][j].setText("*");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MinesweeperV1());
    }
}
