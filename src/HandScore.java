public class HandScore {
    String name;
    double multiplier;

    public HandScore(String name, double multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }

    public String multiplierText() {
        return "x" + valueText();
    }

    public String valueText() {
        if (multiplier == Math.rint(multiplier)) return String.valueOf((int) multiplier);
        return String.valueOf(multiplier);
    }

    public String lesson() {
        return switch (name) {
            case "Pair" -> "Two cards share the same rank.";
            case "Two Pair" -> "You have two different matching pairs.";
            case "Three of a Kind" -> "Three cards share the same rank.";
            case "Straight" -> "Five ranks appear in numerical order.";
            case "Flush" -> "All five cards share the same suit.";
            case "Full House" -> "A three of a kind is combined with a pair.";
            case "Four of a Kind" -> "Four cards share the same rank.";
            case "Straight Flush" -> "Five ordered ranks are all the same suit.";
            case "Royal Flush" -> "10, Jack, Queen, King, and Ace are all the same suit.";
            default -> "No special combination yet; look for matching ranks, ordered ranks, or a shared suit.";
        };
    }
}
