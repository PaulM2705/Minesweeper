package MinesweeperGame;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HighscoreServer {
    private int port;

    public HighscoreServer(int port) {
        this.port = port;
    }


    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Highscore Server started on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     InputStream inputStream = clientSocket.getInputStream()) {

                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    byte[] buffer = inputStream.readAllBytes();
                    String highscoreData = new String(buffer, StandardCharsets.UTF_8);

                    System.out.println("Received highscore data: " + highscoreData);

                    saveHighscoreData(highscoreData);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error starting server: " + e.getMessage());
        }
    }


    private void saveHighscoreData(String highscoreData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscores", true))) {
            writer.write(highscoreData);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

