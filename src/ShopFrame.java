import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShopFrame extends JFrame {
    private static final Color INK = new Color(10, 14, 30);
    private static final Color SURFACE = new Color(24, 31, 56);
    private static final Color SURFACE_LIGHT = new Color(39, 49, 82);
    private static final Color VIOLET = new Color(128, 101, 255);
    private static final Color TEAL = new Color(42, 205, 174);
    private static final Color GOLD = new Color(255, 201, 94);
    private static final Color TEXT = new Color(240, 242, 255);
    private static final int HAT_LIMIT = 5;

    private final Hat[] hats = {
            new Hat("Vanguard Helm", "COMMON", "+15 max HP and +4 block whenever you defend.", 28, 0, 1, 4, 1, 15, 0, 0, 0, 0, 0, false),
            new Hat("Duelist's Plume", "COMMON", "+5 attack on every strike.", 34, 5, 1, 0, 1, 0, 0, 0, 0, 0, 0, false),
            new Hat("Runic Hood", "RARE", "Double all card-powered attack damage.", 58, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, false),
            new Hat("Bastion Bucket", "RARE", "Double the block your cards create.", 58, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, false),
            new Hat("Sunweaver Cap", "UNCOMMON", "Restore 12 HP when each battle begins.", 46, 0, 1, 0, 1, 0, 12, 0, 0, 0, 0, false),
            new Hat("Golden Top Hat", "UNCOMMON", "+20 gold for every victory.", 50, 0, 1, 0, 1, 0, 0, 0, 20, 0, 0, false),
            new Hat("Crimson Bandana", "UNCOMMON", "Restore 14 HP after each victory.", 55, 0, 1, 0, 1, 0, 0, 14, 0, 0, 0, false),
            new Hat("Jester Crown", "RARE", "+3 attack for every card used to attack.", 62, 0, 1, 0, 1, 0, 0, 0, 0, 0, 3, false),
            new Hat("Phoenix Circlet", "LEGENDARY", "The first lethal hit leaves you standing at one-third HP.", 85, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, true),
            new Hat("Sovereign Crown", "LEGENDARY", "+1 combat turn each battle and +3 attack.", 92, 3, 1, 0, 1, 0, 0, 0, 0, 1, 0, false)
    };

    private final Notbalatro mainFrame;
    private final Player player;
    private final Deck deck;
    private JLabel goldLabel;
    private JLabel equippedLabel;
    private JLabel statusLabel;
    private JPanel hatGrid;

    public ShopFrame(Notbalatro mainFrame, Player player, Deck deck) {
        this.mainFrame = mainFrame;
        this.player = player;
        this.deck = deck;

        setTitle("Balnotro: The Hat Emporium");
        setSize(1180, 760);
        setMinimumSize(new Dimension(1040, 680));
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(INK);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));
        setContentPane(root);
        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createHatGrid(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);
        refreshShop();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        JPanel titleArea = new JPanel();
        titleArea.setOpaque(false);
        titleArea.setLayout(new BoxLayout(titleArea, BoxLayout.Y_AXIS));
        titleArea.add(label("BALNOTRO HAT EMPORIUM", new Font("SansSerif", Font.BOLD, 27), GOLD));
        titleArea.add(Box.createVerticalStrut(3));
        titleArea.add(label("Choose a build. Every hat changes how you survive.", new Font("SansSerif", Font.PLAIN, 13), new Color(182, 192, 228)));

        JPanel resources = new JPanel();
        resources.setOpaque(false);
        resources.setLayout(new BoxLayout(resources, BoxLayout.Y_AXIS));
        goldLabel = label("", new Font("SansSerif", Font.BOLD, 19), GOLD);
        goldLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        equippedLabel = label("", new Font("SansSerif", Font.PLAIN, 12), new Color(183, 169, 255));
        equippedLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        resources.add(goldLabel);
        resources.add(Box.createVerticalStrut(4));
        resources.add(equippedLabel);
        header.add(titleArea, BorderLayout.WEST);
        header.add(resources, BorderLayout.EAST);
        return header;
    }

    private JPanel createHatGrid() {
        hatGrid = new JPanel(new GridLayout(2, 5, 12, 12));
        hatGrid.setOpaque(false);
        for (Hat hat : hats) hatGrid.add(createHatCard(hat));
        return hatGrid;
    }

    private JButton createHatCard(Hat hat) {
        String color = rarityColor(hat.rarity);
        String text = "<html><div style='text-align:center; padding: 6px; width: 155px'>"
                + "<span style='color:" + color + "; font-size:10px'><b>" + hat.rarity + "</b></span><br>"
                + "<span style='font-size:15px'><b>" + hat.name + "</b></span><br><br>"
                + "<span style='font-size:10px'>" + hat.description + "</span><br><br>"
                + "<span style='color:#ffc95e'><b>" + hat.cost + " GOLD</b></span></div></html>";
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setForeground(TEXT);
        button.setBackground(SURFACE);
        button.setFocusPainted(false);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setBorder(new CompoundBorder(new LineBorder(colorFromHex(color), 1, true), new EmptyBorder(5, 5, 5, 5)));
        button.addActionListener(e -> buyHat(hat));
        return button;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout(10, 10));
        footer.setOpaque(false);
        JPanel services = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        services.setOpaque(false);
        JButton lifeButton = serviceButton("RESTORE 30 HP", "30 gold", TEAL);
        lifeButton.addActionListener(e -> restoreHealth());
        JButton removeButton = serviceButton("REFINE DECK", "Remove cards", SURFACE_LIGHT);
        removeButton.addActionListener(e -> openRemovalMenu());
        JButton nextButton = serviceButton("BEGIN NEXT BATTLE", "Keep your new hat", VIOLET);
        nextButton.addActionListener(e -> beginNextBattle());
        services.add(lifeButton);
        services.add(removeButton);
        services.add(nextButton);

        statusLabel = label("", new Font("SansSerif", Font.BOLD, 13), new Color(182, 192, 228));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footer.add(statusLabel, BorderLayout.NORTH);
        footer.add(services, BorderLayout.SOUTH);
        return footer;
    }

    private void buyHat(Hat hat) {
        if (player.hats.contains(hat)) {
            statusLabel.setText(hat.name + " is already equipped.");
            return;
        }
        if (player.hats.size() >= HAT_LIMIT) {
            statusLabel.setText("You can equip up to " + HAT_LIMIT + " hats per run.");
            return;
        }
        if (player.gold < hat.cost) {
            statusLabel.setText("You need " + (hat.cost - player.gold) + " more gold for " + hat.name + ".");
            return;
        }
        player.gold -= hat.cost;
        player.equip(hat);
        statusLabel.setText(hat.name + " equipped: " + hat.description);
        refreshShop();
    }

    private void restoreHealth() {
        if (player.gold < 30) {
            statusLabel.setText("You need 30 gold to restore health.");
            return;
        }
        if (player.hp >= player.maxHp) {
            statusLabel.setText("Your HP is already full.");
            return;
        }
        player.gold -= 30;
        player.heal(30);
        statusLabel.setText("You restore 30 HP.");
        refreshShop();
    }

    private void beginNextBattle() {
        dispose();
        mainFrame.setVisible(true);
        mainFrame.startNewRound();
    }

    private void openRemovalMenu() {
        Object[] options = {"Single Card (25 gold)", "Entire Rank (50 gold)", "Entire Suit (80 gold)", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a refinement:", "Refine Deck",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == 0) removeSingleCard();
        if (choice == 1) removeRank();
        if (choice == 2) removeSuit();
    }

    private void removeSingleCard() {
        if (!canRefine(25)) return;
        List<String> cards = new ArrayList<>();
        for (Card card : deck.master) cards.add(card.r + " " + card.s);
        if (cards.isEmpty()) return;
        String selected = (String) JOptionPane.showInputDialog(this, "Select one card:", "Remove Card",
                JOptionPane.QUESTION_MESSAGE, null, cards.toArray(), cards.get(0));
        if (selected != null) {
            player.gold -= 25;
            deck.master.removeIf(card -> (card.r + " " + card.s).equals(selected));
            statusLabel.setText(selected + " removed from your deck.");
            refreshShop();
        }
    }

    private void removeRank() {
        if (!canRefine(50)) return;
        List<String> ranks = new ArrayList<>();
        for (Card card : deck.master) if (!ranks.contains(card.r)) ranks.add(card.r);
        if (ranks.isEmpty()) return;
        String selected = (String) JOptionPane.showInputDialog(this, "Select a rank:", "Remove Rank",
                JOptionPane.QUESTION_MESSAGE, null, ranks.toArray(), ranks.get(0));
        if (selected != null) {
            player.gold -= 50;
            deck.master.removeIf(card -> card.r.equals(selected));
            statusLabel.setText("All " + selected + " cards removed from your deck.");
            refreshShop();
        }
    }

    private void removeSuit() {
        if (!canRefine(80)) return;
        String[] suits = {"♥", "♦", "♣", "♠"};
        String selected = (String) JOptionPane.showInputDialog(this, "Select a suit:", "Remove Suit",
                JOptionPane.QUESTION_MESSAGE, null, suits, suits[0]);
        if (selected != null) {
            player.gold -= 80;
            deck.master.removeIf(card -> card.s.equals(selected));
            statusLabel.setText("All " + selected + " cards removed from your deck.");
            refreshShop();
        }
    }

    private boolean canRefine(int cost) {
        if (player.gold >= cost && !deck.master.isEmpty()) return true;
        statusLabel.setText("You need " + cost + " gold and at least one card to refine the deck.");
        return false;
    }

    private void refreshShop() {
        goldLabel.setText(player.gold + " GOLD     " + player.hp + " / " + player.maxHp + " HP");
        equippedLabel.setText(player.hats.size() + " / " + HAT_LIMIT + " HATS EQUIPPED");
        for (Component component : hatGrid.getComponents()) {
            if (component instanceof JButton button) {
                String hatName = button.getText();
                boolean owned = player.hats.stream().anyMatch(hat -> hatName.contains(hat.name));
                button.setEnabled(!owned && player.hats.size() < HAT_LIMIT);
            }
        }
    }

    private JButton serviceButton(String title, String detail, Color color) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br><span style='font-size:10px'>" + detail + "</span></center></html>");
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(TEXT);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(190, 48));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 12, 5, 12));
        return button;
    }

    private JLabel label(String text, Font font, Color color) {
        JLabel result = new JLabel(text);
        result.setFont(font);
        result.setForeground(color);
        return result;
    }

    private String rarityColor(String rarity) {
        return switch (rarity) {
            case "COMMON" -> "#8fe3d1";
            case "UNCOMMON" -> "#82b8ff";
            case "RARE" -> "#c3a6ff";
            default -> "#ffc95e";
        };
    }

    private Color colorFromHex(String hex) {
        return Color.decode(hex);
    }
}
