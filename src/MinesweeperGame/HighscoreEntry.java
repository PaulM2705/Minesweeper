package MinesweeperGame;

public class HighscoreEntry implements Comparable<HighscoreEntry> {
    private final int score;
    private final String name;

    public HighscoreEntry(int score, String name) {
        this.score = score;
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(HighscoreEntry other) {
        return Integer.compare(other.score, this.score); 
    }

    @Override
    public String toString() {
        return score + " " + name;
    }
}


