package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ScoreManager {

    private int score;
    private String highscore = "";

    private JLabel scoreLabel;
    private JLabel highscoreLabel;
    private JFrame frame;

    public ScoreManager(JFrame frame) {
        this.frame = frame;
        score = 0;
        highscore = GetHighScore();
        initializeScoreLabel();
    }

    public void initializeScoreLabel() {
        scoreLabel = new JLabel("Score: " + score);
        highscoreLabel = new JLabel("Highscore: " + highscore);

        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.add(scoreLabel, BorderLayout.WEST);
        scorePanel.add(highscoreLabel, BorderLayout.EAST);

        frame.add(scorePanel, BorderLayout.NORTH);
    }

    public int calculateExtraPoints(Cell[][] cells, int gridSize) {
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

    public void initializeScore() {
        score = 0;
        highscore = GetHighScore();
        updateScoreLabel();
    }
    public void addToScore(int points) {
        score += points;
    }

    public void updateScoreLabel() {
        if (scoreLabel != null && highscoreLabel != null) {
            scoreLabel.setText("Score: " + score);
            highscoreLabel.setText("Highscore: " + highscore);
        }
    }

    public void updateScore(Cell cell) {
        if (cell.isAMine()) {
            score = 0;
        } else {
            score += cell.getValue();
        }
        updateScoreLabel();
        //CheckScore();
        System.out.println("Current score: " + score);
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
    
    public void resetScore() {
        score = 0;
        updateScoreLabel();
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
}
