import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;
    List<Card> master = new ArrayList<>();
    List<Card> current = new ArrayList<>();

    public void makeMasterDeck() {
        master.clear();
        String[] suits = {"♥", "♦", "♣", "♠"};
        for (String st : suits) {
            for (int i = 2; i <= 10; i++) {
                master.add(new Card("" + i, st, i));
            }
            master.add(new Card("J", st, 10));
            master.add(new Card("Q", st, 10));
            master.add(new Card("K", st, 10));
            master.add(new Card("A", st, 11));
        }
    }

    public void prepareRound() {
        current.clear();
        current.addAll(master);
        Collections.shuffle(current);
    }
}
