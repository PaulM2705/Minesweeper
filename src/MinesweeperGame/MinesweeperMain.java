package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class MinesweeperMain {

    private static final int MINE = 10;
    private static final int SIZE = 500;

    private static Cell[] reusableStorage = new Cell[8];

    private int gridSize = 10; //legt die Größe des Feldes fest
    private int score;
    private String highscore = ""; // Highscore wird hier gespeichert

    private Cell[][] cells;

    private JFrame frame;
    private JButton reset;
    private JButton giveUp;
    private JButton backToMenu;
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
        } else if (source == backToMenu) {
            frame.dispose();
            start();
        } else {
            handleCell((Cell) source);
        }
    };

    private final MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                handleRightClick((Cell) e.getSource());
            }
        }
    };

    private class Cell extends JButton {
        private final int row;
        private final int col;
        private int value;
        private boolean flagged;

        Cell(final int row, final int col, final ActionListener actionListener) {
            this.row = row;
            this.col = col;
            addActionListener(actionListener);
            addMouseListener(mouseAdapter);
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
            setFlagged(false);
        }

        void reveal() {
            if (!flagged) {
                setEnabled(false);
                setText(isAMine() ? "\uD83D\uDCA3\u200B" : String.valueOf(value));
            }
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

        boolean isFlagged() {
            return flagged;
        }

        void setFlagged(boolean flagged) {
            this.flagged = flagged;
            setText(flagged ? "\uD83D\uDEA9" : "");
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

        JPanel gridPanel = new JPanel(new GridBagLayout());
        frame.add(gridPanel, BorderLayout.CENTER);

        initializeButtonPanel();
        initializeGrid(gridPanel);
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
    private int calculateExtraPoints() {
        int extraPoints = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Cell cell = cells[row][col];
                if (cell.isFlagged() && cell.isAMine()) {
                    extraPoints += 5;
                }
            }
        }
        return extraPoints;
    }
    private void initializeScore() {
        score = 0;
        highscore = GetHighScore();
        //initializeScoreLabel();
        updateScoreLabel();
    }

    private void updateScoreLabel() {
        if (scoreLabel != null && highscoreLabel != null) {
            scoreLabel.setText("Score: " + score);
            highscoreLabel.setText("Highscore: " + highscore);
        }
    }

    private void updateScore(Cell cell) {
        if (cell.isAMine()) {
            score = 0;
        } else {
            score += cell.getValue();
        }
        updateScoreLabel();
        System.out.println("Current score: " + score);
    }

    private void initializeButtonPanel() {
        JPanel buttonPanel = new JPanel();

        reset = new JButton("Reset");
        giveUp = new JButton("Give Up");
        backToMenu = new JButton("Back to Menu");

        reset.addActionListener(actionListener);
        giveUp.addActionListener(actionListener);
        backToMenu.addActionListener(actionListener);

        buttonPanel.add(reset);
        buttonPanel.add(giveUp);
        buttonPanel.add(backToMenu);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeGrid(JPanel gridPanel) {
        if (gridSize <= 0) {
            throw new IllegalArgumentException("Grid size must be greater than 0");
        }

        gridPanel.setLayout(new GridLayout(gridSize, gridSize));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(row, col, actionListener);
                //gbc.gridx = col;
                //gbc.gridy = row;
                gridPanel.add(cells[row][col]);//, gbc
            }
        }
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

        final int mineCount = gridSize * gridSize / 30;
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
        if (!cell.isEnabled() || cell.isFlagged()) {
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
            CheckScore();
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
        CheckScore();
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
            int extraPoints = calculateExtraPoints();
            score += extraPoints;
            updateScoreLabel();

            JOptionPane.showMessageDialog(
                    frame, "You have won! Extra points: " + extraPoints, "Congratulations",
                    JOptionPane.INFORMATION_MESSAGE
            );
            CheckScore();
            resetAllCells();
            createMines();
        }
        if (highscore.equals("")) {
            highscore = this.GetHighScore();
        }
    }

    private void handleRightClick(Cell cell) {
        if (!cell.isEnabled()) {
            return;
        }
        cell.setFlagged(!cell.isFlagged());
    }

    public String GetHighScore() {
        String input = "";
        try {
            File file = new File("highscore.dat");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            input = reader.readLine();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input == null ? "" : input;
    }


    public void CheckScore() {
        try {
            int currentHighScore = Integer.parseInt(highscore.split(" ")[0]);
            if (score > currentHighScore) {
                String name = JOptionPane.showInputDialog(
                        frame, "New Highscore! Please enter your name:", "New Highscore",
                        JOptionPane.PLAIN_MESSAGE
                );
                highscore = score + " " + name;
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.dat"));
                    writer.write(highscore);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Falls die Datei leer ist oder der Inhalt nicht korrekt geparst werden kann, initialisiere den Highscore mit dem aktuellen Score
            String name = JOptionPane.showInputDialog(
                    frame, "New Highscore! Please enter your name:", "New Highscore",
                    JOptionPane.PLAIN_MESSAGE
            );
            highscore = score + " " + name;
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.dat"));
                writer.write(highscore);
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

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

        // Feld zur Auswahl der Grid-Größe
        JPanel gridSizePanel = new JPanel();
        gridSizePanel.setLayout(new FlowLayout());

        JLabel gridSizeLabel = new JLabel("Spielfeldgröße: ");
        JTextField gridSizeField = new JTextField(5);

        gridSizePanel.add(gridSizeLabel);
        gridSizePanel.add(gridSizeField);

        startMenuPanel.add(gridSizePanel, BorderLayout.CENTER);

        // Button zum Starten des Spiels
        JButton startButton = new JButton("Spiel starten");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int gridSize = Integer.parseInt(gridSizeField.getText());
                    if (gridSize <= 0) {
                        throw new IllegalArgumentException("Grid-Größe muss größer als 0 sein.");
                    }
                    menuFrame.dispose(); // Schließe Startmenü
                    new MinesweeperMain(gridSize); // Starte das Spiel mit der gewählten Grid-Größe
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(menuFrame, "Bitte geben Sie eine gültige Zahl für die Grid-Größe ein.", "Ungültige Eingabe", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(menuFrame, ex.getMessage(), "Ungültige Eingabe", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        startMenuPanel.add(startButton, BorderLayout.SOUTH);

        menuFrame.add(startMenuPanel, BorderLayout.CENTER);

        menuFrame.setLocationRelativeTo(null);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setVisible(true);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MinesweeperMain().start();
        });
    }
}
