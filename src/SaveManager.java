import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveManager {
    static final int SLOT_COUNT = 3;
    private static final Path SAVE_DIRECTORY = Path.of("saves");

    public static boolean save(GameState gameState, int slot) {
        try {
            Files.createDirectories(SAVE_DIRECTORY);
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(savePath(slot)))) {
                output.writeObject(gameState);
            }
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    public static GameState load(int slot) {
        Path path = savePath(slot);
        if (!Files.exists(path)) return null;
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
            Object savedObject = input.readObject();
            if (savedObject instanceof GameState gameState) return gameState;
        } catch (IOException | ClassNotFoundException exception) {
            return null;
        }
        return null;
    }

    public static boolean exists(int slot) {
        return Files.exists(savePath(slot));
    }

    public static String slotDescription(int slot) {
        GameState gameState = load(slot);
        if (gameState == null) return "EMPTY SLOT";
        String mode = gameState.arcadeMode ? "ARCADE" : "ADVENTURE";
        return mode + "  •  STAGE " + gameState.levels.level + "  •  " + gameState.player.gold + " GOLD  •  "
                + gameState.player.hp + "/" + gameState.player.maxHp + " HP";
    }

    private static Path savePath(int slot) {
        return SAVE_DIRECTORY.resolve("balnotro-slot-" + slot + ".sav");
    }
}
