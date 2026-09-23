import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class MainMenuFrame extends JFrame {
    private static final Color INK = new Color(10, 14, 30);
    private static final Color SURFACE = new Color(24, 31, 56);
    private static final Color SURFACE_LIGHT = new Color(39, 49, 82);
    private static final Color VIOLET = new Color(128, 101, 255);
    private static final Color TEAL = new Color(42, 205, 174);
    private static final Color GOLD = new Color(255, 201, 94);
    private static final Color TEXT = new Color(240, 242, 255);

    public MainMenuFrame() {
        setTitle("Balnotro");
        setSize(980, 690);
        setMinimumSize(new Dimension(850, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);

        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(INK);
        root.setBorder(new EmptyBorder(30, 36, 30, 36));
        setContentPane(root);
        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createContent(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = label("BALNOTRO", new Font("SansSerif", Font.BOLD, 42), GOLD);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = label("CARD-BOUND ROGUELITE", new Font("SansSerif", Font.BOLD, 14), new Color(184, 195, 234));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel tagline = label("Learn the hand. Read the math. Survive the run.", new Font("SansSerif", Font.PLAIN, 15), TEXT);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(2));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(10));
        header.add(tagline);
        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setOpaque(false);
        content.add(createJourneyPanel());
        content.add(createScorePanel());
        return content;
    }

    private JPanel createJourneyPanel() {
        JPanel panel = panelCard();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label("START A JOURNEY", new Font("SansSerif", Font.BOLD, 20), TEAL));
        panel.add(Box.createVerticalStrut(8));
        panel.add(label("Adventure can be saved in any of three slots.", new Font("SansSerif", Font.PLAIN, 13), new Color(185, 196, 234)));
        panel.add(Box.createVerticalStrut(16));

        JButton newJourney = menuButton("NEW ADVENTURE", "Three lives and permanent save slots", VIOLET);
        newJourney.addActionListener(e -> openNewRun(false));
        JButton arcade = menuButton("ARCADE MODE", "One life, endless stages, top-five scores", new Color(183, 88, 188));
        arcade.addActionListener(e -> openNewRun(true));
        panel.add(newJourney);
        panel.add(Box.createVerticalStrut(10));
        panel.add(arcade);
        panel.add(Box.createVerticalStrut(22));
        panel.add(label("LOAD A SAVE", new Font("SansSerif", Font.BOLD, 16), GOLD));
        panel.add(Box.createVerticalStrut(8));
        for (int slot = 1; slot <= SaveManager.SLOT_COUNT; slot++) {
            panel.add(createSaveButton(slot));
            if (slot < SaveManager.SLOT_COUNT) panel.add(Box.createVerticalStrut(7));
        }
        return panel;
    }

    private JPanel createScorePanel() {
        JPanel panel = panelCard();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label("ARCADE HIGHSCORES", new Font("SansSerif", Font.BOLD, 20), GOLD));
        panel.add(Box.createVerticalStrut(8));
        panel.add(label("One life. How far can you climb?", new Font("SansSerif", Font.PLAIN, 13), new Color(185, 196, 234)));
        panel.add(Box.createVerticalStrut(16));

        List<HighScoreEntry> scores = HighScoreManager.loadScores();
        for (int rank = 1; rank <= 5; rank++) {
            String text;
            if (rank <= scores.size()) {
                HighScoreEntry entry = scores.get(rank - 1);
                text = String.format("%d.  %,d POINTS     STAGE %d", rank, entry.score, entry.stage);
            } else {
                text = rank + ".  ---";
            }
            JLabel score = label(text, new Font("Monospaced", Font.BOLD, 15), rank == 1 ? GOLD : TEXT);
            score.setAlignmentX(Component.LEFT_ALIGNMENT);
            score.setBorder(new CompoundBorder(new LineBorder(SURFACE_LIGHT, 1, true), new EmptyBorder(12, 14, 12, 14)));
            score.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            score.setBackground(new Color(29, 37, 65));
            score.setOpaque(true);
            panel.add(score);
            panel.add(Box.createVerticalStrut(8));
        }
        panel.add(Box.createVerticalGlue());
        JLabel scoreRule = label("Score = stages cleared × 1,000 + gold × 10 + remaining HP", new Font("SansSerif", Font.PLAIN, 12), new Color(178, 188, 226));
        scoreRule.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scoreRule);
        return panel;
    }

    private JButton createSaveButton(int slot) {
        boolean available = SaveManager.exists(slot);
        JButton button = menuButton("SLOT " + slot, SaveManager.slotDescription(slot), available ? SURFACE_LIGHT : new Color(31, 38, 66));
        button.setEnabled(available);
        button.addActionListener(e -> loadSave(slot));
        return button;
    }

    private JButton menuButton(String title, String detail, Color color) {
        JButton button = new JButton("<html><div style='text-align:left; padding-left:8px; width:340px'><b>" + title
                + "</b><br><span style='font-size:10px'>" + detail + "</span></div></html>");
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(TEXT);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        button.setPreferredSize(new Dimension(360, 58));
        button.setBorder(new EmptyBorder(5, 12, 5, 12));
        return button;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        JLabel text = label("ADVENTURE SAVES YOUR CURRENT RUN. ARCADE RECORDS ONLY YOUR BEST SCORES.",
                new Font("SansSerif", Font.BOLD, 11), new Color(156, 170, 214));
        footer.add(text);
        return footer;
    }

    private JPanel panelCard() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(new CompoundBorder(new LineBorder(SURFACE_LIGHT, 1, true), new EmptyBorder(22, 22, 22, 22)));
        return panel;
    }

    private JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void openNewRun(boolean arcadeMode) {
        dispose();
        new Notbalatro(arcadeMode).setVisible(true);
    }

    private void loadSave(int slot) {
        GameState gameState = SaveManager.load(slot);
        if (gameState == null) {
            JOptionPane.showMessageDialog(this, "That save could not be opened.", "Load Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
        new Notbalatro(gameState).setVisible(true);
    }
}
