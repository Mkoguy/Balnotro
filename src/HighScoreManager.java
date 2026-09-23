import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    private static final Path SCORE_FILE = Path.of("saves", "balnotro-arcade-scores.sav");

    public static List<HighScoreEntry> loadScores() {
        if (!Files.exists(SCORE_FILE)) return new ArrayList<>();
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(SCORE_FILE))) {
            Object savedObject = input.readObject();
            if (savedObject instanceof List<?> savedList) {
                List<HighScoreEntry> scores = new ArrayList<>();
                for (Object entry : savedList) {
                    if (entry instanceof HighScoreEntry highScoreEntry) scores.add(highScoreEntry);
                }
                scores.sort(Comparator.comparingInt((HighScoreEntry entry) -> entry.score).reversed());
                return scores;
            }
        } catch (IOException | ClassNotFoundException exception) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public static void record(int score, int stage) {
        List<HighScoreEntry> scores = loadScores();
        scores.add(new HighScoreEntry(score, stage));
        scores.sort(Comparator.comparingInt((HighScoreEntry entry) -> entry.score).reversed());
        if (scores.size() > 5) scores = new ArrayList<>(scores.subList(0, 5));
        try {
            Files.createDirectories(SCORE_FILE.getParent());
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(SCORE_FILE))) {
                output.writeObject(scores);
            }
        } catch (IOException exception) {
        }
    }
}
