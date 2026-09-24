import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PracticeDialog extends JDialog {
    private static final Color INK = new Color(10, 14, 30);
    private static final Color SURFACE = new Color(24, 31, 56);
    private static final Color SURFACE_LIGHT = new Color(39, 49, 82);
    private static final Color GOLD = new Color(255, 201, 94);
    private static final Color TEAL = new Color(42, 205, 174);
    private static final Color TEXT = new Color(240, 242, 255);
    private static final String[] HAND_NAMES = {
            "High Card", "Pair", "Two Pair", "Three of a Kind", "Straight",
            "Flush", "Full House", "Four of a Kind", "Straight Flush", "Royal Flush"
    };

    private final List<Card[]> challenges = createChallenges();
    private final List<String> missed = new ArrayList<>();
    private final Object previousButtonDisabledText;
    private int questionIndex;
    private int correctCount;
    private int correctDamageCount;
    private boolean revealed;
    private JLabel progressLabel;
    private JLabel questionLabel;
    private JLabel feedbackLabel;
    private JPanel cardsPanel;
    private JPanel answerHolder;
    private JLabel lockedAnswerLabel;
    private JComboBox<String> answerBox;
    private JTextField damageField;
    private JButton checkButton;
    private JButton nextButton;

    public PracticeDialog(JFrame owner) {
        super(owner, "Balnotro: Practice Lab", true);
        previousButtonDisabledText = UIManager.get("Button.disabledText");
        UIManager.put("Button.disabledText", new Color(58, 64, 85));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                UIManager.put("Button.disabledText", previousButtonDisabledText);
            }
        });
        setSize(780, 585);
        setMinimumSize(new Dimension(720, 540));
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(INK);
        root.setBorder(new EmptyBorder(25, 30, 25, 30));
        setContentPane(root);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        JLabel title = label("PRACTICE LAB", 28, Font.BOLD, GOLD);
        titleGroup.add(title);
        titleGroup.add(Box.createVerticalStrut(4));
        titleGroup.add(label("Name the hand. Predict the damage. Learn why.", 14, Font.PLAIN, TEXT));
        progressLabel = label("", 14, Font.BOLD, TEAL);
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        heading.add(titleGroup, BorderLayout.WEST);
        heading.add(progressLabel, BorderLayout.EAST);
        root.add(heading, BorderLayout.NORTH);

        JPanel exercise = new JPanel(new BorderLayout(0, 18));
        exercise.setBackground(SURFACE);
        exercise.setBorder(new CompoundBorder(new LineBorder(SURFACE_LIGHT, 1, true), new EmptyBorder(24, 20, 22, 20)));
        questionLabel = label("", 18, Font.BOLD, TEXT);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 13, 10));
        cardsPanel.setOpaque(false);
        feedbackLabel = label("", 14, Font.PLAIN, TEXT);
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackLabel.setVerticalAlignment(SwingConstants.CENTER);
        feedbackLabel.setOpaque(true);
        feedbackLabel.setBackground(SURFACE_LIGHT);
        feedbackLabel.setBorder(new EmptyBorder(12, 14, 12, 14));
        feedbackLabel.setPreferredSize(new Dimension(680, 112));
        exercise.add(questionLabel, BorderLayout.NORTH);
        exercise.add(cardsPanel, BorderLayout.CENTER);
        exercise.add(feedbackLabel, BorderLayout.SOUTH);
        root.add(exercise, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        controls.setOpaque(false);
        answerBox = new JComboBox<>();
        answerBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        answerBox.setPreferredSize(new Dimension(215, 42));
        answerBox.addActionListener(e -> checkButton.setEnabled(answerBox.getSelectedIndex() > 0 && !revealed));
        answerHolder = new JPanel(new CardLayout());
        answerHolder.setOpaque(false);
        answerHolder.setPreferredSize(new Dimension(215, 42));
        lockedAnswerLabel = label("", 14, Font.BOLD, new Color(58, 64, 85));
        lockedAnswerLabel.setOpaque(true);
        lockedAnswerLabel.setBackground(new Color(226, 232, 242));
        lockedAnswerLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
        answerHolder.add(answerBox, "choose");
        answerHolder.add(lockedAnswerLabel, "locked");
        JLabel damageLabel = label("DMG?", 12, Font.BOLD, GOLD);
        damageField = new JTextField();
        damageField.setFont(new Font("SansSerif", Font.BOLD, 15));
        damageField.setHorizontalAlignment(SwingConstants.CENTER);
        damageField.setPreferredSize(new Dimension(65, 42));
        damageField.setToolTipText("Predict attack damage with hero x1 and no hats");
        damageField.addActionListener(e -> revealAnswer());
        checkButton = button("CHECK ANSWER", TEAL);
        checkButton.addActionListener(e -> revealAnswer());
        nextButton = button("NEXT HAND", new Color(128, 101, 255));
        nextButton.addActionListener(e -> {
            if (questionIndex == challenges.size()) restart();
            else advance();
        });
        controls.add(answerHolder);
        controls.add(damageLabel);
        controls.add(damageField);
        controls.add(checkButton);
        controls.add(nextButton);
        root.add(controls, BorderLayout.SOUTH);
        showQuestion();
    }

    private void showQuestion() {
        revealed = false;
        Card[] cards = challenges.get(questionIndex);
        progressLabel.setText("HAND " + (questionIndex + 1) + " / " + challenges.size());
        questionLabel.setText("Name the hand and predict its attack damage.");
        cardsPanel.removeAll();
        for (Card card : cards) cardsPanel.add(cardView(card));
        cardsPanel.revalidate();
        cardsPanel.repaint();

        List<String> choices = new ArrayList<>();
        choices.add(HAND_NAMES[questionIndex]);
        choices.add(HAND_NAMES[(questionIndex + 1) % HAND_NAMES.length]);
        choices.add(HAND_NAMES[(questionIndex + 3) % HAND_NAMES.length]);
        choices.add(HAND_NAMES[(questionIndex + 6) % HAND_NAMES.length]);
        Collections.shuffle(choices);
        answerBox.removeAllItems();
        answerBox.addItem("Choose a hand...");
        for (String choice : choices) answerBox.addItem(choice);
        answerBox.setSelectedIndex(0);
        ((CardLayout) answerHolder.getLayout()).show(answerHolder, "choose");
        damageField.setText("");
        damageField.setEnabled(true);
        checkButton.setEnabled(false);
        nextButton.setEnabled(false);
        feedbackLabel.setBackground(SURFACE_LIGHT);
        feedbackLabel.setForeground(TEXT);
        feedbackLabel.setText("<html><center>Add the card power, then use the hand multiplier.<br>Assume hero ×1, no hats, and round to the nearest whole number.</center></html>");
    }

    private void revealAnswer() {
        if (revealed || answerBox.getSelectedIndex() <= 0) return;
        int predictedDamage;
        try {
            predictedDamage = Integer.parseInt(damageField.getText().trim());
            if (predictedDamage < 0) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            feedbackLabel.setBackground(new Color(86, 61, 40));
            feedbackLabel.setText("<html><center>Enter a whole-number damage prediction, then check your answer.</center></html>");
            damageField.requestFocusInWindow();
            return;
        }
        revealed = true;
        Hand hand = new Hand();
        int power = 0;
        for (Card card : challenges.get(questionIndex)) {
            hand.sel.add(card);
            power += card.val;
        }
        HandScore result = hand.evaluateSelection();
        boolean correct = result.name.equals(answerBox.getSelectedItem());
        if (correct) correctCount++;
        else missed.add(result.name);
        int boostedPower = (int) Math.round(power * result.multiplier);
        boolean correctDamage = predictedDamage == boostedPower;
        if (correctDamage) correctDamageCount++;
        String choiceFeedback = correct ? "" : "You chose " + answerBox.getSelectedItem() + ". ";
        feedbackLabel.setBackground(correct && correctDamage ? new Color(20, 69, 67) : new Color(86, 40, 59));
        feedbackLabel.setForeground(TEXT);
        feedbackLabel.setText("<html><center><b>HAND " + (correct ? "✓" : "✗") + ": " + result.name.toUpperCase()
                + " " + result.multiplierText() + " &nbsp; • &nbsp; DAMAGE " + (correctDamage ? "✓" : "✗") + "</b><br>"
                + result.lesson() + "<br>" + choiceFeedback + power + " card power × " + result.valueText() + " = " + boostedPower
                + " damage. You predicted " + predictedDamage + ".</center></html>");
        lockedAnswerLabel.setText(String.valueOf(answerBox.getSelectedItem()));
        ((CardLayout) answerHolder.getLayout()).show(answerHolder, "locked");
        damageField.setEnabled(false);
        checkButton.setEnabled(false);
        nextButton.setEnabled(true);
        nextButton.setText(questionIndex == challenges.size() - 1 ? "SEE RESULTS" : "NEXT HAND");
    }

    private void advance() {
        if (!revealed) return;
        questionIndex++;
        if (questionIndex == challenges.size()) {
            showResults();
        } else {
            showQuestion();
        }
    }

    private void showResults() {
        progressLabel.setText("SESSION COMPLETE");
        questionLabel.setText("You named " + correctCount + "/10 hands and predicted " + correctDamageCount + "/10 attacks.");
        cardsPanel.removeAll();
        cardsPanel.add(label(correctCount == challenges.size() ? "★  PERFECT RUN  ★" : "KEEP PRACTICING", 24, Font.BOLD, GOLD));
        cardsPanel.revalidate();
        cardsPanel.repaint();
        feedbackLabel.setBackground(SURFACE_LIGHT);
        feedbackLabel.setText("<html><center>" + (missed.isEmpty()
                ? "You recognized every hand. Try applying them in battle."
                : "Review these hands next: " + String.join(", ", missed) + ".")
                + "<br>Practice never changes your adventure.</center></html>");
        answerBox.setSelectedIndex(0);
        lockedAnswerLabel.setText("Session complete");
        ((CardLayout) answerHolder.getLayout()).show(answerHolder, "locked");
        damageField.setEnabled(false);
        checkButton.setEnabled(false);
        nextButton.setText("PRACTICE AGAIN");
    }

    private void restart() {
        questionIndex = 0;
        correctCount = 0;
        correctDamageCount = 0;
        missed.clear();
        nextButton.setText("NEXT HAND");
        showQuestion();
    }

    private JPanel cardView(Card card) {
        boolean redSuit = card.s.equals("♥") || card.s.equals("♦");
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(100, 132));
        panel.setBackground(new Color(248, 246, 238));
        panel.setBorder(new CompoundBorder(new LineBorder(GOLD, 2, true), new EmptyBorder(10, 9, 8, 9)));
        JLabel rank = label(card.r + card.s, 24, Font.BOLD, redSuit ? new Color(175, 42, 67) : new Color(25, 33, 54));
        JLabel suit = label(card.s, 36, Font.BOLD, redSuit ? new Color(175, 42, 67) : new Color(25, 33, 54));
        suit.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel power = label(card.val + " POWER", 10, Font.BOLD, new Color(65, 71, 89));
        power.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(rank, BorderLayout.NORTH);
        panel.add(suit, BorderLayout.CENTER);
        panel.add(power, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        return label;
    }

    private JButton button(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(INK);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(160, 42));
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(color.brighter(), 1, true));
        return button;
    }

    static List<Card[]> createChallenges() {
        List<Card[]> result = new ArrayList<>();
        result.add(cards("2♣", "7♦", "J♥"));
        result.add(cards("8♣", "8♦", "K♥"));
        result.add(cards("4♣", "4♥", "J♦", "J♠", "9♣"));
        result.add(cards("6♣", "6♥", "6♦", "K♠"));
        result.add(cards("3♣", "4♥", "5♦", "6♠", "7♣"));
        result.add(cards("2♥", "5♥", "8♥", "J♥", "K♥"));
        result.add(cards("9♣", "9♥", "9♦", "Q♣", "Q♦"));
        result.add(cards("A♣", "A♦", "A♥", "A♠", "3♦"));
        result.add(cards("5♠", "6♠", "7♠", "8♠", "9♠"));
        result.add(cards("10♦", "J♦", "Q♦", "K♦", "A♦"));
        return result;
    }

    private static Card[] cards(String... codes) {
        Card[] cards = new Card[codes.length];
        for (int i = 0; i < codes.length; i++) {
            String code = codes[i];
            String rank = code.substring(0, code.length() - 1);
            String suit = code.substring(code.length() - 1);
            int power = rank.equals("A") ? 11 : (rank.equals("J") || rank.equals("Q") || rank.equals("K") ? 10 : Integer.parseInt(rank));
            cards[i] = new Card(rank, suit, power);
        }
        return cards;
    }
}
