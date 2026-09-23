import java.io.Serializable;

public class HighScoreEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    int score;
    int stage;

    public HighScoreEntry(int score, int stage) {
        this.score = score;
        this.stage = stage;
    }
}
