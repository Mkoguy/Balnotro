import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class Hand implements Serializable {
    private static final long serialVersionUID = 1L;
    static final int HAND_SIZE = 8;
    List<Card> cards = new ArrayList<>();
    List<Card> sel = new ArrayList<>();

    public void draw(Deck d) {
        while (cards.size() < HAND_SIZE) {
            cards.add(null);
        }
        for (int i = 0; i < cards.size() && !d.current.isEmpty(); i++) {
            if (cards.get(i) == null) {
                cards.set(i, d.current.remove(0));
            }
        }
    }

    public void removeSelectedCards() {
        for (Card card : sel) {
            int slot = cards.indexOf(card);
            if (slot >= 0) cards.set(slot, null);
        }
        sel.clear();
    }

    public HandScore evaluateSelection() {
        if (sel.isEmpty()) return new HandScore("No Hand", 1);

        Map<String, Integer> ranks = new HashMap<>();
        Map<String, Integer> suits = new HashMap<>();
        List<Integer> rankValues = new ArrayList<>();
        for (Card card : sel) {
            ranks.put(card.r, ranks.getOrDefault(card.r, 0) + 1);
            suits.put(card.s, suits.getOrDefault(card.s, 0) + 1);
            rankValues.add(rankValue(card.r));
        }

        int pairCount = 0;
        boolean threeOfKind = false;
        boolean fourOfKind = false;
        for (int count : ranks.values()) {
            if (count == 2) pairCount++;
            if (count == 3) threeOfKind = true;
            if (count == 4) fourOfKind = true;
        }

        boolean fiveCards = sel.size() == 5;
        boolean flush = fiveCards && suits.size() == 1;
        boolean straight = isStraight(rankValues);
        boolean royal = flush && rankValues.contains(10) && rankValues.contains(11) && rankValues.contains(12)
                && rankValues.contains(13) && rankValues.contains(14);

        if (royal) return new HandScore("Royal Flush", 8);
        if (straight && flush) return new HandScore("Straight Flush", 5);
        if (fourOfKind) return new HandScore("Four of a Kind", 4);
        if (threeOfKind && pairCount == 1) return new HandScore("Full House", 3);
        if (flush) return new HandScore("Flush", 2.5);
        if (straight) return new HandScore("Straight", 2.25);
        if (threeOfKind) return new HandScore("Three of a Kind", 1.75);
        if (pairCount >= 2) return new HandScore("Two Pair", 1.5);
        if (pairCount == 1) return new HandScore("Pair", 1.25);
        return new HandScore("High Card", 1);
    }

    private boolean isStraight(List<Integer> ranks) {
        if (ranks.size() != 5) return false;
        List<Integer> sorted = new ArrayList<>(ranks);
        sorted.sort(Integer::compareTo);
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).equals(sorted.get(i - 1))) return false;
        }
        boolean standardStraight = sorted.get(4) - sorted.get(0) == 4;
        boolean aceLowStraight = sorted.get(0) == 2 && sorted.get(1) == 3 && sorted.get(2) == 4
                && sorted.get(3) == 5 && sorted.get(4) == 14;
        return standardStraight || aceLowStraight;
    }

    private int rankValue(String rank) {
        return switch (rank) {
            case "J" -> 11;
            case "Q" -> 12;
            case "K" -> 13;
            case "A" -> 14;
            default -> Integer.parseInt(rank);
        };
    }
}
