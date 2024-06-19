package MinesweeperGame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
    	new Thread(() -> {
    		int port = 9000;
    		HighscoreServer server = new HighscoreServer(port);
    		server.startServer();
    		}).start();
    	
        SwingUtilities.invokeLater(() -> {
            new Menu();
        });
    }
}
