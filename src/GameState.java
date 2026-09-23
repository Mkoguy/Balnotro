import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    Player player;
    LevelManager levels;
    Deck deck;
    Hand hand;
    Enemy enemy;
    boolean arcadeMode;

    public GameState(Player player, LevelManager levels, Deck deck, Hand hand, Enemy enemy, boolean arcadeMode) {
        this.player = player;
        this.levels = levels;
        this.deck = deck;
        this.hand = hand;
        this.enemy = enemy;
        this.arcadeMode = arcadeMode;
    }
}
