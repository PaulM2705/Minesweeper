package MinesweeperGame;

import java.io.*;
import java.net.*;
import java.util.*;

public class LeaderboardServer {
    private int port;
    private ScoreManager scoreManager;

    public LeaderboardServer(int port) {
        this.port = port;
        this.scoreManager = new ScoreManager();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Leaderboard server started on port " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String command = in.readLine();
                    if (command.equals("SUBMIT")) {
                        String playerName = in.readLine();
                        int score = Integer.parseInt(in.readLine());
                        System.out.println("Received score submission: " + score + " from " + playerName);

                        // Add score to highscore list
                        scoreManager.checkScore(new LeaderboardEntry(playerName, score));
                    } else if (command.equals("GET")) {
                        System.out.println("Received request for top scores.");

                        // Get top scores from highscore list
                        List<LeaderboardEntry> topEntries = scoreManager.getHighscoreEntries();
                        for (LeaderboardEntry entry : topEntries) {
                            out.println(entry.getScore() + " " + entry.getPlayerName());
                        }
                        for (int i = topEntries.size(); i < 5; i++) {
                            out.println("0 Nobody");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 9000; // Beispielport, den Sie verwenden möchten
        LeaderboardServer server = new LeaderboardServer(port);
        server.startServer();
    }
}
