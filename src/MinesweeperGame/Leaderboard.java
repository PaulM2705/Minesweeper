/*package MinesweeperGame;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard implements Serializable {
    private static final long serialVersionUID = 1L; // Zur Versionskontrolle der Serialisierung
    private static final String LEADERBOARD_FILE = "leaderboard.dat";

    private List<LeaderboardEntry> entries;

    public Leaderboard() {
        entries = new ArrayList<>();
        loadEntries();
    }

    public List<LeaderboardEntry> getEntries() {
        return entries;
    }

    public void addEntry(LeaderboardEntry entry) {
        entries.add(entry);
        Collections.sort(entries, (e1, e2) -> Integer.compare(e2.getScore(), e1.getScore()));
        trimToTop5();
        saveEntries();
    }

    private void trimToTop5() {
        if (entries.size() > 5) {
            entries = entries.subList(0, 5);
        }
    }

    private void loadEntries() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LEADERBOARD_FILE))) {
            entries = (List<LeaderboardEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Wenn leaderboard.dat nicht existiert, wird eine leere Liste verwendet
            entries = new ArrayList<>();
        }
    }

    private void saveEntries() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/