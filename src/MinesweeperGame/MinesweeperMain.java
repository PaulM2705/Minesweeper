package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class MinesweeperMain {

    private static final int MINE = 10;
    private static final int SIZE = 500;
    private static final double POPULATION_CONSTANT = 1.5;

    private static final String HIGHSCORE_FILE = "highscore.txt";

    private static Cell[] reusableStorage = new Cell[8];

    private int gridSize;
    private int score;
    private int highscore;

    private Cell[][] cells;

    private JFrame frame;
    private JButton reset;
    private JButton giveUp;
    private JLabel scoreLabel;

    private final ActionListener actionListener = actionEvent -> {
        Object source = actionEvent.getSource();
        if (source == reset) {
            createMines();
        } else if (source == giveUp) {
            revealBoardAndDisplay("You gave up.");
        } else {
            handleCell((Cell) source);
        }
    };

    private class Cell extends JButton {
        private final int row;
        private final int col;
        private int value;

        Cell(final int row, final int col, final ActionListener actionListener) {
            this.row = row;
            this.col = col;
            addActionListener(actionListener);
            setText("");
        }

        int getValue() {
            return value;
        }

        void setValue(int value) {
            this.value = value;
        }

        boolean isAMine() {
            return value == MINE;
        }

        void reset() {
            setValue(0);
            setEnabled(true);
            setText("");
        }

        void reveal() {
            setEnabled(false);
            setText(isAMine() ? "\uD83D\uDCA3\u200B" : String.valueOf(value));
        }

        void updateNeighbourCount() {
            getNeighbours(reusableStorage);
            for (Cell neighbour : reusableStorage) {
                if (neighbour == null) {
                    break;
                }
                if (neighbour.isAMine()) {
                    value++;
                }
            }
        }

        void getNeighbours(final Cell[] container) {
            // Alle Elemente null setzen
            for (int i = 0; i < reusableStorage.length; i++) {
                reusableStorage[i] = null;
            }

            int index = 0;

            for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                for (int colOffset = -1; colOffset <= 1; colOffset++) {
                    if (rowOffset == 0 && colOffset == 0) {
                        continue;
                    }
                    int rowValue = row + rowOffset;
                    int colValue = col + colOffset;

                    if (rowValue < 0 || rowValue >= gridSize || colValue < 0 || colValue >= gridSize) {
                        continue;
                    }

                    container[index++] = cells[rowValue][colValue];
                }
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Cell cell = (Cell) obj;
            return row == cell.row && col == cell.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    private MinesweeperMain(final int gridSize) {
        this.gridSize = gridSize;
        cells = new Cell[gridSize][gridSize];

        frame = new JFrame("Minesweeper");
        frame.setSize(SIZE, SIZE);
        frame.setLayout(new BorderLayout());

        initializeButtonPanel();
        initializeGrid();
        initializeScore();
        initializeScoreLabel();
        loadHighscore();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    private void initializeScoreLabel() {
        scoreLabel = new JLabel("Score: " + score + "   Highscore: " + highscore);
        frame.add(scoreLabel, BorderLayout.NORTH);
    }

    private void initializeScore() {
        score = 0;
        highscore = 0;
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score + "   Highscore: " + highscore);
    }

    private void updateScore(Cell cell) {
        if (cell.isAMine()) {
            score = 0;
        } else {
            score += cell.getValue();
        }
        updateScoreLabel();
    }

    private void initializeButtonPanel() {
        JPanel buttonPanel = new JPanel();

        reset = new JButton("Reset");
        giveUp = new JButton("Give Up");

        reset.addActionListener(actionListener);
        giveUp.addActionListener(actionListener);

        buttonPanel.add(reset);
        buttonPanel.add(giveUp);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeGrid() {
        Container grid = new Container();
        grid.setLayout(new GridLayout(gridSize, gridSize));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(row, col, actionListener);
                grid.add(cells[row][col]);
            }
        }
        createMines();
        frame.add(grid, BorderLayout.CENTER);
    }

    private void resetAllCells() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reset();
            }
        }
    }

    private void createMines() {
        resetAllCells();

        final int mineCount = (int) POPULATION_CONSTANT * gridSize;
        final Random random = new Random();

        // Map all (row, col) pairs to unique integers
        Set<Integer> positions = new HashSet<>(gridSize * gridSize);
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                positions.add(row * gridSize + col);
            }
        }

        // Mienen Initialisieren
        for (int index = 0; index < mineCount; index++) {
            int choice = random.nextInt(positions.size());
            int row = choice / gridSize;
            int col = choice % gridSize;
            cells[row][col].setValue(MINE);
            positions.remove(choice);
        }

        //  Anzahl Nachbarn Initialisieren
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (!cells[row][col].isAMine()) {
                    cells[row][col].updateNeighbourCount();
                }
            }
        }
    }

    private void handleCell(Cell cell) {
        if (!cell.isEnabled()) {
            return;
        }
        if (cell.isAMine()) {
            cell.setForeground(Color.RED);
            cell.reveal();
            updateScore(cell);
            revealBoardAndDisplay("You clicked on a mine.");
        } else {
            cell.reveal();
            updateScore(cell);
            if (cell.getValue() == 0) {
                Set<Cell> positions = new HashSet<>();
                positions.add(cell);
                cascade(positions);
            }
        }checkForWin();
    }

    private void loadHighscore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:/Users/piet_/IdeaProjects/SpielProg2/src/highscore.txt"));
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                highscore = Integer.parseInt(line);
            }
            reader.close();
        } catch (IOException | NumberFormatException e) {
            // Fehler beim Laden des Highscores
            e.printStackTrace();
        }
    }


    private void revealBoardAndDisplay(String message) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (cells[row][col].isEnabled()) {
                    cells[row][col].reveal();
                }
            }
        }

        JOptionPane.showMessageDialog(
                frame, message, "Game Over",
                JOptionPane.ERROR_MESSAGE
        );

        createMines();
    }

    private void cascade(Set<Cell> positionsToClear) {
        while (!positionsToClear.isEmpty()) {
            Cell cell = positionsToClear.iterator().next();
            positionsToClear.remove(cell);
            cell.reveal();
            updateScore(cell);

            cell.getNeighbours(reusableStorage);
            for (Cell neighbour : reusableStorage) {
                if (neighbour == null) {
                    break;
                }
                if (neighbour.getValue() == 0 && neighbour.isEnabled()) {
                    positionsToClear.add(neighbour);
                } else if (neighbour.isEnabled()) {
                    neighbour.reveal();
                    updateScore(neighbour);
                }
            }
        }
    }

    private void checkForWin() {
        boolean won = true;
        outer:
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                if (!cell.isAMine() && cell.isEnabled()) {
                    won = false;
                    break outer;
                }
            }
        }

        if (won) {
            JOptionPane.showMessageDialog(
                    frame, "You have won!", "Congratulations",
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (score > highscore) {
                highscore = score;
                saveHighscore();
            }
        }
    }
    private void saveHighscore() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE));
            writer.write(String.valueOf(highscore));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace(); // Fehlerbehandlung für das Schreiben des Highscores
        }
    }


    private static void run(final int gridSize) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
        }
        new MinesweeperMain(gridSize);
    }

    public static void main(String[] args) {
        final int gridSize = 10;
        SwingUtilities.invokeLater(() -> MinesweeperMain.run(gridSize));
    }
}
