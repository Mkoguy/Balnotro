import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class QuestionSettings {
    public enum Mode { OFF, LOCAL, ONLINE }

    private static final Path SETTINGS_FILE = Path.of(".balnotro-settings.properties");
    private static final String KEY = "combatQuestionMode";

    private QuestionSettings() {}

    public static Mode getMode() {
        Properties properties = new Properties();
        if (Files.exists(SETTINGS_FILE)) {
            try (InputStream input = Files.newInputStream(SETTINGS_FILE)) {
                properties.load(input);
            } catch (IOException exception) {
                return Mode.OFF;
            }
        }
        try {
            return Mode.valueOf(properties.getProperty(KEY, Mode.OFF.name()));
        } catch (IllegalArgumentException exception) {
            return Mode.OFF;
        }
    }

    public static boolean setMode(Mode mode) {
        Properties properties = new Properties();
        properties.setProperty(KEY, mode.name());
        try (OutputStream output = Files.newOutputStream(SETTINGS_FILE)) {
            properties.store(output, "Balnotro settings");
            return true;
        } catch (IOException exception) {
            return false;
        }
    }
}
