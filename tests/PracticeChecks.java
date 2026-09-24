import java.util.List;

public class PracticeChecks {
    public static void main(String[] args) {
        String[] expected = {
                "High Card", "Pair", "Two Pair", "Three of a Kind", "Straight",
                "Flush", "Full House", "Four of a Kind", "Straight Flush", "Royal Flush"
        };
        int[] expectedDamage = {19, 33, 56, 49, 56, 88, 141, 188, 175, 408};
        List<Card[]> challenges = PracticeDialog.createChallenges();
        if (challenges.size() != expected.length) throw new AssertionError("Expected ten practice hands");
        for (int i = 0; i < expected.length; i++) {
            Hand hand = new Hand();
            int power = 0;
            for (Card card : challenges.get(i)) {
                hand.sel.add(card);
                power += card.val;
            }
            HandScore score = hand.evaluateSelection();
            String actual = score.name;
            if (!expected[i].equals(actual)) {
                throw new AssertionError("Hand " + (i + 1) + ": expected " + expected[i] + ", got " + actual);
            }
            int damage = (int) Math.round(power * score.multiplier);
            if (damage != expectedDamage[i]) {
                throw new AssertionError("Hand " + (i + 1) + ": expected " + expectedDamage[i]
                        + " damage, got " + damage);
            }
        }
        System.out.println("All ten practice hands and damage calculations match the game evaluator.");
    }
}
