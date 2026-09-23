import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class TutorialDialog extends JDialog {
    private static final Color INK = new Color(10, 14, 30);
    private static final Color SURFACE = new Color(24, 31, 56);
    private static final Color SURFACE_LIGHT = new Color(39, 49, 82);
    private static final Color VIOLET = new Color(128, 101, 255);
    private static final Color GOLD = new Color(255, 201, 94);
    private static final Color TEXT = new Color(240, 242, 255);

    private final String[][] pages = {
            {"WELCOME TO BALNOTRO", "Build poker hands to battle your way through a card-powered RPG. Each battle begins with eight cards, an enemy, and a choice: deal damage or stay alive."},
            {"ONE TURN, TWO CHOICES", "Select one to five cards, then ATTACK to damage the enemy or DEFEND to create a shield. Read the enemy's NEXT attack before deciding. Your shield lasts for its next strike only."},
            {"THE MATH IS THE MAGIC", "Your card power is added together, then multiplied by your hero multiplier and poker-hand multiplier. Hat bonuses are added afterward. The live equation below your cards shows every step."},
            {"LEARN POKER HANDS", "Pair ×1.25  •  Two Pair ×1.5  •  Three of a Kind ×1.75\nStraight ×2.25  •  Flush ×2.5  •  Full House ×3\nFour of a Kind ×4  •  Straight Flush ×5  •  Royal Flush ×8"},
            {"GROW YOUR BUILD", "Win battles for gold, then visit the Hat Emporium. Hats can improve health, attack, defense, healing, gold rewards, or turn count. Build around the poker hands you enjoy making."}
    };

    private int pageIndex = 0;
    private JLabel heading;
    private JLabel lesson;
    private JLabel progress;
    private JButton backButton;
    private JButton nextButton;

    public TutorialDialog(JFrame owner) {
        super(owner, "Balnotro: How to Play", true);
        setSize(690, 410);
        setResizable(false);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(INK);
        root.setBorder(new EmptyBorder(26, 30, 24, 30));
        setContentPane(root);

        JPanel lessonCard = new JPanel();
        lessonCard.setBackground(SURFACE);
        lessonCard.setBorder(new CompoundBorder(new LineBorder(SURFACE_LIGHT, 1, true), new EmptyBorder(26, 30, 26, 30)));
        lessonCard.setLayout(new BoxLayout(lessonCard, BoxLayout.Y_AXIS));
        heading = new JLabel();
        heading.setFont(new Font("SansSerif", Font.BOLD, 25));
        heading.setForeground(GOLD);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        lesson = new JLabel();
        lesson.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lesson.setForeground(TEXT);
        lesson.setAlignmentX(Component.LEFT_ALIGNMENT);
        lessonCard.add(heading);
        lessonCard.add(Box.createVerticalStrut(16));
        lessonCard.add(lesson);
        root.add(lessonCard, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);
        progress = new JLabel();
        progress.setForeground(new Color(178, 188, 226));
        progress.setFont(new Font("SansSerif", Font.BOLD, 13));
        backButton = button("BACK", SURFACE_LIGHT);
        nextButton = button("NEXT", VIOLET);
        backButton.addActionListener(e -> changePage(-1));
        nextButton.addActionListener(e -> changePage(1));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(backButton);
        buttons.add(nextButton);
        controls.add(progress, BorderLayout.WEST);
        controls.add(buttons, BorderLayout.EAST);
        root.add(controls, BorderLayout.SOUTH);
        refreshPage();
    }

    private JButton button(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(TEXT);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(105, 40));
        return button;
    }

    private void changePage(int change) {
        if (pageIndex == pages.length - 1 && change > 0) {
            dispose();
            return;
        }
        pageIndex += change;
        refreshPage();
    }

    private void refreshPage() {
        heading.setText(pages[pageIndex][0]);
        lesson.setText("<html><div style='width:560px; line-height:1.45'>" + pages[pageIndex][1].replace("\n", "<br>") + "</div></html>");
        progress.setText("LESSON " + (pageIndex + 1) + " OF " + pages.length);
        backButton.setEnabled(pageIndex > 0);
        nextButton.setText(pageIndex == pages.length - 1 ? "START RUN" : "NEXT");
    }
}
