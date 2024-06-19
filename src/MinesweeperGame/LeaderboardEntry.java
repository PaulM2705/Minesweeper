package MinesweeperGame;

import java.io.Serializable;

public class LeaderboardEntry implements Serializable {
    private String playerName;
    private int score;

    public LeaderboardEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return playerName + ": " + score;
    }
}

