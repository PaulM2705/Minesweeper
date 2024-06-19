package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {

    private int score;
    private List<HighscoreEntry> highscores;

    private JLabel scoreLabel;
    private JLabel highscoreLabel;
    private JFrame frame;
    public final int HIGH_SCORES_COUNT = 5;

    public ScoreManager(JFrame frame) {
        this.frame = frame;
        score = 0;
        highscores = loadHighscores();
        initializeScoreLabel();
    }

    public void initializeScoreLabel() {
        scoreLabel = new JLabel("Score: " + score);
        highscoreLabel = new JLabel("Highscore: " + getHighscoreString(", "));

        Font labelFont = new Font("Serif", Font.ITALIC, 20);
        scoreLabel.setFont(labelFont);
        highscoreLabel.setFont(labelFont);

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
        updateScoreLabel();
    }

    public void addToScore(int points) {
        score += points;
    }

    public void updateScoreLabel() {
        if (scoreLabel != null && highscoreLabel != null) {
            scoreLabel.setText("Score: " + score);
            highscoreLabel.setText("Highscores: " + getHighscoreString(", "));
        }
    }

    public void updateScore(Cell cell) {
        if (cell.isAMine()) {
            score = 0;
        } else {
            score += cell.getValue();
        }
        updateScoreLabel();
        System.out.println("Current score: " + score);
    }

    private List<HighscoreEntry> loadHighscores() {
        List<HighscoreEntry> highscores = new ArrayList<>();
        try {
            File file = new File("highscore.dat");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                int score = Integer.parseInt(parts[0]);
                String name = parts[1];
                highscores.add(new HighscoreEntry(score, name));
            }
            reader.close();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        Collections.sort(highscores);
        return highscores;
    }

    private void saveHighscores() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.dat"));
            for (HighscoreEntry entry : highscores) {
                writer.write(entry.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkScore() {
        if (highscores.size() < HIGH_SCORES_COUNT || score > highscores.get(highscores.size() - 1).getScore()) {
            String name = JOptionPane.showInputDialog(frame, "New Highscore! Please enter your name:", "New Highscore", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.trim().isEmpty()) {
                HighscoreEntry newEntry = new HighscoreEntry(score, name);
                highscores.add(newEntry);
                Collections.sort(highscores);
                if (highscores.size() > HIGH_SCORES_COUNT) {
                    highscores.remove(highscores.size() - 1);
                }
                saveHighscores();
                sendHighscoresToServer();
            }
        }
    }

    public void sendHighscoresToServer() {
        try (Socket socket = new Socket("localhost", 9000);
             OutputStream outputStream = socket.getOutputStream()) {

            StringBuilder sb = new StringBuilder();
            for (HighscoreEntry entry : highscores) {
                sb.append(entry.getScore()).append(" ").append(entry.getName()).append("\n");
            }

            String highscoreData = sb.toString();
            outputStream.write(highscoreData.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            System.out.println("Highscores successfully sent to server.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error sending highscores to server: " + e.getMessage());
        }
    }

    public void resetScore() {
        score = 0;
        updateScoreLabel();
    }

    private String getHighscoreString(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (HighscoreEntry entry : highscores) {
            sb.append(entry.getScore()).append(" ").append(entry.getName()).append(delimiter);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - delimiter.length());
        }
        return sb.toString().trim();
    }
}
