package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class MinesweeperMain {

    private static final int MINE = 10;
    private static final int SIZE = 500;

    private static Cell[] reusableStorage = new Cell[8];

    private int gridSize = 10; //legt die Größe des Feldes fest
    private int score;
    private int highscore = 0; // Highscore wird hier gespeichert

    private Cell[][] cells;

    private JFrame frame;
    private JButton reset;
    private JButton giveUp;
    private JLabel scoreLabel;
    private JLabel highscoreLabel;

    private final ActionListener actionListener = actionEvent -> {
        Object source = actionEvent.getSource();
        if (source == reset) {
            score = 0;
            createMines();
            updateScoreLabel();
        } else if (source == giveUp) {
            revealBoardAndDisplay("You gave up.");
            score = 0;
            updateScoreLabel();
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
            Arrays.fill(container, null);

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

    private MinesweeperMain(int gridSize) {
        this.gridSize = gridSize;
        cells = new Cell[gridSize][gridSize];

        frame = new JFrame("Minesweeper");
        frame.setSize(SIZE, SIZE);
        frame.setLayout(new BorderLayout());

        initializeButtonPanel();
        initializeGrid();
        initializeScore();
        initializeScoreLabel();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public MinesweeperMain() {
	// TODO Auto-generated constructor stub
}
private void initializeScoreLabel() {
        scoreLabel = new JLabel("Score: " + score);
        highscoreLabel = new JLabel("Highscore: " + highscore);

        JPanel scorePanel = new JPanel(new BorderLayout());

        scorePanel.add(scoreLabel, BorderLayout.WEST);
        scorePanel.add(highscoreLabel, BorderLayout.EAST);

        frame.add(scorePanel, BorderLayout.NORTH);
    }

    private void initializeScore() {
        score = 0;
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
        highscoreLabel.setText("Highscore: " + highscore);
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
        if (gridSize <= 0) {
            throw new IllegalArgumentException("Grid size must be greater than 0");
        }

        Container grid = new Container();
        grid.setLayout(new GridLayout(gridSize, gridSize));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(row, col, actionListener);
                grid.add(cells[row][col]);
            }
        }
        frame.add(grid, BorderLayout.CENTER);
        createMines();
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

        final int mineCount = (gridSize * gridSize / 10);
        final Random random = new Random();

        Set<Integer> minePositions = new HashSet<>();
        while (minePositions.size() < mineCount) {
            int pos = random.nextInt(gridSize * gridSize);
            minePositions.add(pos);
        }

        for (int pos : minePositions) {
            int row = pos / gridSize;
            int col = pos % gridSize;
            cells[row][col].setValue(MINE);
        }

        // Anzahl Nachbarn Initialisieren
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
            JOptionPane.showMessageDialog(
                    frame, "You clicked on a mine.", "Game Over",
                    JOptionPane.ERROR_MESSAGE
            );
            resetAllCells(); // Reset cells
            createMines();
        } else {
            cell.reveal();
            updateScore(cell);
            if (cell.getValue() == 0) {
                Set<Cell> positions = new HashSet<>();
                positions.add(cell);
                cascade(positions);
            }
        }
        checkForWinOrLoss();
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
        checkForWinOrLoss();
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

    private void checkForWinOrLoss() {
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
        }

        if (score > highscore) {
            highscore = score;
            updateScoreLabel();
        }
    }

    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Starte das Spiel
            new MinesweeperMain().start();
        });
    }*/

    // Methode zum Starten des Spiels und Anzeigen des Startmenüs
    void start() {
        JFrame menuFrame = new JFrame("Minesweeper Start Menu");
        menuFrame.setSize(SIZE, SIZE);
        menuFrame.setLayout(new BorderLayout());

        // Erstelle das Startmenü-Panel
        JPanel startMenuPanel = new JPanel();
        startMenuPanel.setLayout(new BorderLayout());

        // Willkommensnachricht
        JLabel welcomeLabel = new JLabel("Willkommen bei Minesweeper von Piet Grube und Paul Mahrt");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        startMenuPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Buttons für die Levelauswahl
        JPanel levelButtonPanel = new JPanel();
        levelButtonPanel.setLayout(new GridLayout(3, 1));

        JButton level1Button = new JButton("Level 1");
        JButton level2Button = new JButton("Level 2");
        JButton level3Button = new JButton("Level 3");

        // ActionListener für die Levelauswahl
        ActionListener levelActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Bestimme das gewählte Level
                int gridSize = 0;
                if (e.getSource() == level1Button) {
                    gridSize = 5; // Beispiel: Level 1 hat eine Gittergröße von 5x5
                } else if (e.getSource() == level2Button) {
                    gridSize = 8; // Beispiel: Level 2 hat eine Gittergröße von 8x8
                } else if (e.getSource() == level3Button) {
                    gridSize = 10; // Beispiel: Level 3 hat eine Gittergröße von 10x10
                }

                // Starte das Spiel mit dem gewählten Level
                menuFrame.dispose(); // Schließe das Startmenü
                new MinesweeperMain(gridSize); // Starte das Spiel mit dem gewählten Level
            }
        };


        level1Button.addActionListener(levelActionListener);
        level2Button.addActionListener(levelActionListener);
        level3Button.addActionListener(levelActionListener);

        levelButtonPanel.add(level1Button);
        levelButtonPanel.add(level2Button);
        levelButtonPanel.add(level3Button);

        startMenuPanel.add(levelButtonPanel, BorderLayout.CENTER);


        menuFrame.add(startMenuPanel, BorderLayout.CENTER);

        menuFrame.setLocationRelativeTo(null);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MinesweeperMain().start();
        });
    }}
