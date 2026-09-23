import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Notbalatro extends JFrame {
    private static final Color INK = new Color(10, 14, 30);
    private static final Color SURFACE = new Color(24, 31, 56);
    private static final Color SURFACE_LIGHT = new Color(39, 49, 82);
    private static final Color VIOLET = new Color(128, 101, 255);
    private static final Color TEAL = new Color(42, 205, 174);
    private static final Color RED = new Color(246, 91, 111);
    private static final Color GOLD = new Color(255, 201, 94);
    private static final Color TEXT = new Color(240, 242, 255);
    private static final int MAX_SELECTED_CARDS = 5;

    Deck deck;
    Hand hand;
    Player player;
    LevelManager levels;
    Enemy enemy;

    private JLabel playerNameLabel;
    private JLabel playerStatsLabel;
    private JLabel shieldLabel;
    private JLabel enemyNameLabel;
    private JLabel enemyStatsLabel;
    private JLabel intentLabel;
    private JLabel hatsLabel;
    private JLabel statusLabel;
    private JLabel selectionLabel;
    private JLabel mathLabel;
    private JLabel learningLabel;
    private JProgressBar playerHpBar;
    private JProgressBar enemyHpBar;
    private JPanel handPanel;
    private JButton attackButton;
    private JButton defendButton;
    private JButton discardButton;
    private JButton selectAllButton;
    private boolean battleResolved;
    private final boolean arcadeMode;
    private String lastActionMath = "Select cards to preview your attack and defense math.";

    public Notbalatro() {
        this(false);
    }

    public Notbalatro(boolean arcadeMode) {
        this(null, arcadeMode);
    }

    public Notbalatro(GameState gameState) {
        this(gameState, gameState.arcadeMode);
    }

    private Notbalatro(GameState gameState, boolean arcadeMode) {
        this.arcadeMode = arcadeMode;
        setupTheme();
        setTitle(arcadeMode ? "Balnotro: Arcade" : "Balnotro: Cardbound RPG");
        setSize(1120, 760);
        setMinimumSize(new Dimension(950, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);

        if (gameState == null) {
            player = new Player("The Super Awesome AMAZING Player");
            levels = new LevelManager();
            deck = new Deck();
            hand = new Hand();
            if (arcadeMode) player.lives = 1;
            deck.makeMasterDeck();
            startNewRound();
        } else {
            player = gameState.player;
            levels = gameState.levels;
            deck = gameState.deck;
            hand = gameState.hand;
            enemy = gameState.enemy;
            hand.sel.clear();
        }

        JPanel root = new JPanel(new BorderLayout(16, 14));
        root.setBackground(INK);
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBattlefield(), BorderLayout.CENTER);
        root.add(createActionArea(), BorderLayout.SOUTH);

        updateView();
        if (gameState == null) SwingUtilities.invokeLater(this::showTutorial);
    }

    private void setupTheme() {
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Panel.background", INK);
        UIManager.put("Button.select", SURFACE_LIGHT);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);

        JPanel titleArea = new JPanel();
        titleArea.setOpaque(false);
        titleArea.setLayout(new BoxLayout(titleArea, BoxLayout.Y_AXIS));
        JLabel title = label("BALNOTRO", new Font("SansSerif", Font.BOLD, 28), GOLD);
        JLabel subtitle = label("A CARD-BOUND ROGUELITE", new Font("SansSerif", Font.BOLD, 11), new Color(174, 183, 220));
        titleArea.add(title);
        titleArea.add(Box.createVerticalStrut(2));
        titleArea.add(subtitle);

        hatsLabel = label("", new Font("SansSerif", Font.BOLD, 13), new Color(183, 169, 255));
        hatsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JButton tutorialButton = compactButton("HOW TO PLAY");
        tutorialButton.addActionListener(e -> showTutorial());
        JButton saveButton = compactButton(arcadeMode ? "EXIT ARCADE" : "SAVE & MENU");
        saveButton.addActionListener(e -> saveOrExit());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        controls.add(tutorialButton);
        controls.add(saveButton);
        header.add(titleArea, BorderLayout.WEST);
        header.add(hatsLabel, BorderLayout.CENTER);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JPanel createBattlefield() {
        JPanel battlefield = new JPanel(new BorderLayout(14, 14));
        battlefield.setOpaque(false);

        JPanel combatants = new JPanel(new GridLayout(1, 2, 14, 0));
        combatants.setOpaque(false);
        combatants.add(createPlayerPanel());
        combatants.add(createEnemyPanel());

        JPanel middle = new JPanel(new BorderLayout(0, 12));
        middle.setOpaque(false);
        statusLabel = label("", new Font("SansSerif", Font.BOLD, 16), TEXT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new CompoundBorder(new LineBorder(SURFACE_LIGHT, 1, true), new EmptyBorder(13, 16, 13, 16)));
        statusLabel.setBackground(SURFACE);
        statusLabel.setOpaque(true);

        JPanel handContainer = panelCard();
        handContainer.setLayout(new BorderLayout(0, 8));
        JPanel handHeading = new JPanel(new BorderLayout());
        handHeading.setOpaque(false);
        JLabel handTitle = label("YOUR HAND", new Font("SansSerif", Font.BOLD, 14), TEXT);
        selectionLabel = label("Select up to 5 cards", new Font("SansSerif", Font.PLAIN, 13), new Color(174, 183, 220));
        selectionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        handHeading.add(handTitle, BorderLayout.WEST);
        handHeading.add(selectionLabel, BorderLayout.EAST);

        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        handPanel.setOpaque(false);
        learningLabel = label("", new Font("SansSerif", Font.PLAIN, 13), new Color(178, 188, 226));
        learningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        handContainer.add(handHeading, BorderLayout.NORTH);
        handContainer.add(handPanel, BorderLayout.CENTER);
        handContainer.add(learningLabel, BorderLayout.SOUTH);

        middle.add(statusLabel, BorderLayout.NORTH);
        middle.add(handContainer, BorderLayout.CENTER);

        battlefield.add(combatants, BorderLayout.NORTH);
        battlefield.add(middle, BorderLayout.CENTER);
        return battlefield;
    }

    private JPanel createPlayerPanel() {
        JPanel panel = panelCard();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        playerNameLabel = label("", new Font("SansSerif", Font.BOLD, 18), TEAL);
        playerStatsLabel = label("", new Font("SansSerif", Font.PLAIN, 13), new Color(190, 200, 235));
        shieldLabel = label("", new Font("SansSerif", Font.BOLD, 13), new Color(111, 199, 255));
        playerHpBar = progressBar(TEAL, new Color(17, 65, 67));

        panel.add(playerNameLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(playerHpBar);
        panel.add(Box.createVerticalStrut(7));
        panel.add(playerStatsLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(shieldLabel);
        return panel;
    }

    private JPanel createEnemyPanel() {
        JPanel panel = panelCard();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        enemyNameLabel = label("", new Font("SansSerif", Font.BOLD, 18), RED);
        enemyStatsLabel = label("", new Font("SansSerif", Font.PLAIN, 13), new Color(235, 190, 202));
        intentLabel = label("", new Font("SansSerif", Font.BOLD, 13), new Color(255, 151, 102));
        enemyHpBar = progressBar(RED, new Color(73, 27, 43));

        panel.add(enemyNameLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(enemyHpBar);
        panel.add(Box.createVerticalStrut(7));
        panel.add(enemyStatsLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(intentLabel);
        return panel;
    }

    private JPanel createActionArea() {
        JPanel actions = new JPanel(new BorderLayout(10, 0));
        actions.setOpaque(false);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonRow.setOpaque(false);
        attackButton = actionButton("ATTACK", "Deal damage", VIOLET);
        defendButton = actionButton("DEFEND", "Gain block", TEAL);
        discardButton = actionButton("DISCARD", "Redraw cards", SURFACE_LIGHT);
        selectAllButton = actionButton("SELECT ALL", "Maximum 5 cards", new Color(80, 91, 143));
        attackButton.addActionListener(e -> attack());
        defendButton.addActionListener(e -> defend());
        discardButton.addActionListener(e -> discard());
        selectAllButton.addActionListener(e -> selectAllCards());
        buttonRow.add(selectAllButton);
        buttonRow.add(attackButton);
        buttonRow.add(defendButton);
        buttonRow.add(discardButton);

        mathLabel = label("", new Font("SansSerif", Font.BOLD, 12), new Color(211, 218, 249));
        mathLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel help = label("Choose cards, then choose one action. Your shield only lasts for this enemy strike.",
                new Font("SansSerif", Font.PLAIN, 12), new Color(157, 169, 208));
        help.setHorizontalAlignment(SwingConstants.CENTER);
        actions.add(mathLabel, BorderLayout.NORTH);
        actions.add(buttonRow, BorderLayout.CENTER);
        actions.add(help, BorderLayout.SOUTH);
        return actions;
    }

    private JPanel panelCard() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(new CompoundBorder(new LineBorder(SURFACE_LIGHT, 1, true), new EmptyBorder(13, 15, 13, 15)));
        return panel;
    }

    private JProgressBar progressBar(Color foreground, Color background) {
        JProgressBar progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setForeground(foreground);
        progress.setBackground(background);
        progress.setBorder(new LineBorder(SURFACE_LIGHT, 1, true));
        progress.setFont(new Font("SansSerif", Font.BOLD, 12));
        progress.setAlignmentX(Component.LEFT_ALIGNMENT);
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return progress;
    }

    private JLabel label(String text, Font font, Color color) {
        JLabel result = new JLabel(text);
        result.setFont(font);
        result.setForeground(color);
        result.setAlignmentX(Component.LEFT_ALIGNMENT);
        return result;
    }

    private JButton actionButton(String title, String detail, Color color) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br><span style='font-size:10px'>" + detail + "</span></center></html>");
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(174, 54));
        button.setForeground(TEXT);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 14, 5, 14));
        return button;
    }

    private JButton compactButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(TEXT);
        button.setBackground(SURFACE_LIGHT);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(122, 34));
        button.setBorder(new LineBorder(new Color(89, 103, 151), 1, true));
        return button;
    }

    private void showHand() {
        handPanel.removeAll();
        for (Card card : hand.cards) {
            if (card == null) {
                JPanel emptySlot = new JPanel();
                emptySlot.setOpaque(false);
                emptySlot.setPreferredSize(new Dimension(88, 118));
                handPanel.add(emptySlot);
                continue;
            }
            boolean redSuit = card.s.equals("♥") || card.s.equals("♦");
            Color suitColor = redSuit ? new Color(255, 117, 138) : new Color(221, 227, 255);
            JToggleButton button = new JToggleButton("<html><center><span style='font-size:22px'><b>" + card.r + "</b></span><br>"
                    + "<span style='font-size:24px'>" + card.s + "</span><br><span style='font-size:10px'>" + card.val + " POWER</span></center></html>");
            button.setFont(new Font("SansSerif", Font.BOLD, 15));
            button.setForeground(suitColor);
            button.setBackground(new Color(31, 39, 69));
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(88, 118));
            button.setBorder(new LineBorder(hand.sel.contains(card) ? GOLD : SURFACE_LIGHT, hand.sel.contains(card) ? 3 : 1, true));
            button.setSelected(hand.sel.contains(card));
            button.addActionListener(e -> selectCard(card, button));
            handPanel.add(button);
        }
        handPanel.revalidate();
        handPanel.repaint();
    }

    private void selectCard(Card card, JToggleButton button) {
        if (button.isSelected()) {
            if (hand.sel.size() >= MAX_SELECTED_CARDS) {
                button.setSelected(false);
                statusLabel.setText("Choose up to " + MAX_SELECTED_CARDS + " cards for your action.");
            } else {
                hand.sel.add(card);
            }
        } else {
            hand.sel.remove(card);
        }
        showHand();
        updateSelectionLabel();
    }

    private void selectAllCards() {
        if (battleResolved || player.hands <= 0) return;
        hand.sel.clear();
        for (Card card : hand.cards) {
            if (card != null && hand.sel.size() < MAX_SELECTED_CARDS) hand.sel.add(card);
        }
        statusLabel.setText("Selected " + hand.sel.size() + " cards. Choose ATTACK or DEFEND.");
        updateView();
    }

    private int selectedPower() {
        int total = 0;
        for (Card card : hand.sel) total += card.val;
        return total;
    }

    private boolean canUseAction() {
        if (battleResolved) return false;
        if (player.hands <= 0) {
            statusLabel.setText("You are out of turns. Win before your next round begins.");
            return false;
        }
        if (hand.sel.isEmpty()) {
            statusLabel.setText("Select one to " + MAX_SELECTED_CARDS + " cards first.");
            return false;
        }
        return true;
    }

    private void attack() {
        if (!canUseAction()) return;
        int cardPower = selectedPower();
        HandScore handScore = hand.evaluateSelection();
        int damage = calculateAttack(cardPower, hand.sel.size(), handScore);
        lastActionMath = buildAttackMath(cardPower, hand.sel.size(), handScore, damage, "LAST ATTACK");
        enemy.takeDamage(damage);
        spendSelectedCards();
        player.hands--;
        statusLabel.setText("You strike for " + damage + " damage.");
        finishTurn();
    }

    private void defend() {
        if (!canUseAction()) return;
        int cardPower = selectedPower();
        HandScore handScore = hand.evaluateSelection();
        int block = calculateBlock(cardPower, handScore);
        lastActionMath = buildDefenseMath(cardPower, handScore, block, "LAST DEFEND");
        player.gainBlock(block);
        spendSelectedCards();
        player.hands--;
        statusLabel.setText("You raise a guard of " + block + ".");
        finishTurn();
    }

    private void discard() {
        if (battleResolved) return;
        if (player.discards <= 0) {
            statusLabel.setText("No redraws remain this round.");
            return;
        }
        if (hand.sel.isEmpty()) {
            statusLabel.setText("Select cards to discard.");
            return;
        }
        player.discards--;
        lastActionMath = "LAST REDRAW: " + hand.sel.size() + " selected cards replaced without spending a turn.";
        spendSelectedCards();
        statusLabel.setText("You redraw your hand without spending a turn.");
        updateView();
    }

    private void spendSelectedCards() {
        hand.removeSelectedCards();
        hand.draw(deck);
    }

    private void finishTurn() {
        if (enemy.isDead()) {
            winBattle();
            return;
        }
        enemyTurn();
        if (player.isDefeated()) {
            loseLife();
            return;
        }
        if (player.hands <= 0) {
            statusLabel.setText("No turns left. The enemy survives this round.");
            updateView();
            Timer pause = new Timer(900, e -> loseLife());
            pause.setRepeats(false);
            pause.start();
            return;
        }
        updateView();
    }

    private void enemyTurn() {
        int incoming = enemy.getNextAttack();
        int blockBeforeHit = player.block;
        boolean reviveAvailable = player.canRevive();
        int damage = player.takeDamage(incoming);
        boolean revived = reviveAvailable && player.reviveUsed;
        player.block = 0;
        enemy.advanceTurn();
        if (revived) {
            statusLabel.setText("Phoenix Circlet restores you to " + player.hp + " HP!");
        } else if (damage == 0) {
            statusLabel.setText("Your guard absorbs " + incoming + " damage (" + blockBeforeHit + " block). ");
        } else {
            statusLabel.setText("" + enemy.name + " hits for " + damage + " damage.");
        }
    }

    private void winBattle() {
        battleResolved = true;
        int reward = enemy.reward + player.getGoldBonus();
        int heal = player.getVictoryHeal();
        levels.advanceLevel(player, enemy);
        String recovery = heal > 0 ? " and recover " + heal + " HP" : "";
        statusLabel.setText("Victory! You earned " + reward + " gold" + recovery + ".");
        updateView();
        Timer pause = new Timer(700, e -> {
            setVisible(false);
            new ShopFrame(this, player, deck).setVisible(true);
        });
        pause.setRepeats(false);
        pause.start();
    }

    private void loseLife() {
        player.lives--;
        if (arcadeMode) {
            int stagesCleared = Math.max(0, levels.level - 1);
            int score = (stagesCleared * 1000) + (player.gold * 10) + player.hp;
            HighScoreManager.record(score, stagesCleared);
            JOptionPane.showMessageDialog(this, "Arcade run over!\nScore: " + score + "\nStages cleared: " + stagesCleared,
                    "Arcade Score Recorded", JOptionPane.INFORMATION_MESSAGE);
            returnToMenu();
            return;
        }
        if (player.lives > 0) {
            player.hp = player.maxHp;
            JOptionPane.showMessageDialog(this, "The run pushes you back, but you rise again.\nLives remaining: " + player.lives,
                    "A Life Lost", JOptionPane.WARNING_MESSAGE);
            startNewRound();
        } else {
            JOptionPane.showMessageDialog(this, "The journey ends at stage " + levels.level + ".\nA new run begins.",
                    "Run Over", JOptionPane.ERROR_MESSAGE);
            levels.reset();
            player = new Player("The Wayfarer");
            deck.makeMasterDeck();
            startNewRound();
        }
    }

    public void startNewRound() {
        battleResolved = false;
        lastActionMath = "Select cards to preview your attack and defense math.";
        enemy = levels.generateNextEnemy();
        player.resetResources();
        deck.prepareRound();
        hand.cards.clear();
        hand.sel.clear();
        hand.draw(deck);
        if (statusLabel != null) {
            statusLabel.setText("Stage " + levels.level + ": read the enemy intent and choose your cards.");
            updateView();
        }
    }

    private void updateSelectionLabel() {
        if (selectionLabel != null) selectionLabel.setText(hand.sel.size() + " / " + MAX_SELECTED_CARDS + " cards selected");
        updateMathPreview();
        updateLearningTip();
    }

    private void updateMathPreview() {
        if (mathLabel == null) return;
        if (hand.sel.isEmpty()) {
            mathLabel.setText(lastActionMath);
            return;
        }
        int cardPower = selectedPower();
        int count = hand.sel.size();
        HandScore handScore = hand.evaluateSelection();
        int damage = calculateAttack(cardPower, count, handScore);
        int block = calculateBlock(cardPower, handScore);
        mathLabel.setText("<html>HAND: " + handScore.name.toUpperCase() + " " + handScore.multiplierText()
                + "&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp; ATTACK: " + buildAttackMath(cardPower, count, handScore, damage, "")
                + "&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp; DEFEND: " + buildDefenseMath(cardPower, handScore, block, "") + "</html>");
    }

    private int calculateAttack(int cardPower, int cardCount, HandScore handScore) {
        return (int) Math.round(cardPower * player.getAttackMultiplier() * handScore.multiplier)
                + player.getAttackBonus() + (cardCount * player.getPerCardBonus());
    }

    private int calculateBlock(int cardPower, HandScore handScore) {
        return (int) Math.round(cardPower * player.getDefenseMultiplier() * handScore.multiplier)
                + player.getDefenseBonus();
    }

    private String buildAttackMath(int cardPower, int cardCount, HandScore handScore, int damage, String title) {
        String prefix = title.isEmpty() ? "" : title + ": ";
        return prefix + "(" + cardPower + " × " + player.getAttackMultiplier() + " × " + handScore.valueText() + ") + "
                + player.getAttackBonus() + " + (" + cardCount + " × " + player.getPerCardBonus() + ") = " + damage + " DMG";
    }

    private String buildDefenseMath(int cardPower, HandScore handScore, int block, String title) {
        String prefix = title.isEmpty() ? "" : title + ": ";
        return prefix + "(" + cardPower + " × " + player.getDefenseMultiplier() + " × " + handScore.valueText() + ") + "
                + player.getDefenseBonus() + " = " + block + " BLOCK";
    }

    private void updateLearningTip() {
        if (learningLabel == null) return;
        if (hand.sel.isEmpty()) {
            learningLabel.setText("LEARN: Select cards to identify a poker hand. Matching ranks make pairs; five matching suits make a flush.");
            return;
        }
        HandScore handScore = hand.evaluateSelection();
        learningLabel.setText("LEARN — " + handScore.name.toUpperCase() + " " + handScore.multiplierText()
                + ": " + handScore.lesson());
    }

    private void showTutorial() {
        TutorialDialog tutorial = new TutorialDialog(this);
        tutorial.setVisible(true);
    }

    private void saveOrExit() {
        if (!arcadeMode && battleResolved) {
            statusLabel.setText("Finish the victory shop before saving your adventure.");
            return;
        }
        if (arcadeMode) {
            returnToMenu();
            return;
        }
        Object[] options = {"Save Slot 1", "Save Slot 2", "Save Slot 3", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a slot for your current adventure.", "Save Adventure",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice < 0 || choice >= SaveManager.SLOT_COUNT) return;
        if (SaveManager.save(createGameState(), choice + 1)) {
            returnToMenu();
        } else {
            JOptionPane.showMessageDialog(this, "The save could not be created.", "Save Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveAndReturnToMenu() {
        saveOrExit();
    }

    private GameState createGameState() {
        return new GameState(player, levels, deck, hand, enemy, arcadeMode);
    }

    private void returnToMenu() {
        dispose();
        new MainMenuFrame().setVisible(true);
    }

    private void updateView() {
        playerNameLabel.setText(player.name.toUpperCase());
        String mode = arcadeMode ? "ARCADE  |  " : "";
        playerStatsLabel.setText(mode + "Stage " + levels.level + "  |  " + player.hands + " turns  |  " + player.discards
                + " redraws  |  " + player.gold + " gold  |  " + player.lives + " lives");
        shieldLabel.setText(player.block > 0 ? "Shield: " + player.block : "Shield: none");
        playerHpBar.setMaximum(player.maxHp);
        playerHpBar.setValue(player.hp);
        playerHpBar.setString(player.hp + " / " + player.maxHp + " HP");

        enemyNameLabel.setText(enemy.name.toUpperCase());
        enemyStatsLabel.setText("Reward: " + enemy.reward + " gold  |  Turn " + (enemy.turnCount + 1));
        intentLabel.setText("NEXT: " + enemy.getIntent().toUpperCase());
        enemyHpBar.setMaximum(enemy.maxHp);
        enemyHpBar.setValue(enemy.hp);
        enemyHpBar.setString(enemy.hp + " / " + enemy.maxHp + " HP");

        StringBuilder equipped = new StringBuilder("EQUIPPED  ");
        if (player.hats.isEmpty()) {
            equipped.append("No hats yet");
        } else {
            for (Hat hat : player.hats) equipped.append("• ").append(hat.name).append("  ");
        }
        hatsLabel.setText(equipped.toString());
        attackButton.setEnabled(!battleResolved && player.hands > 0);
        defendButton.setEnabled(!battleResolved && player.hands > 0);
        discardButton.setEnabled(!battleResolved && player.discards > 0 && player.hands > 0);
        selectAllButton.setEnabled(!battleResolved && player.hands > 0 && !hand.cards.isEmpty());
        updateSelectionLabel();
        showHand();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
