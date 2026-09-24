import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsDialog extends JDialog {
    public SettingsDialog(JFrame owner) {
        super(owner, "Balnotro: Settings", true);
        setSize(490, 330);
        setResizable(false);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(new Color(10, 14, 30));
        root.setBorder(new EmptyBorder(24, 28, 24, 28));
        setContentPane(root);

        JLabel title = new JLabel("COMBAT QUESTIONS");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(255, 201, 94));
        root.add(title, BorderLayout.NORTH);

        JPanel choices = new JPanel();
        choices.setOpaque(false);
        choices.setLayout(new BoxLayout(choices, BoxLayout.Y_AXIS));
        ButtonGroup group = new ButtonGroup();
        JRadioButton off = choice("Off — play without questions", QuestionSettings.Mode.OFF, group, choices);
        JRadioButton local = choice("Local — questions from questions.txt", QuestionSettings.Mode.LOCAL, group, choices);
        JRadioButton online = choice("Online AI — story questions from OpenAI", QuestionSettings.Mode.ONLINE, group, choices);
        switch (QuestionSettings.getMode()) {
            case OFF -> off.setSelected(true);
            case LOCAL -> local.setSelected(true);
            case ONLINE -> online.setSelected(true);
        }
        choices.add(Box.createVerticalStrut(12));
        JLabel note = new JLabel("<html>Correct answers unlock ATTACK or DEFEND. Wrong answers<br>do not spend a turn. Online AI requires OPENAI_API_KEY<br>and uses local questions if the service is unavailable.</html>");
        note.setFont(new Font("SansSerif", Font.PLAIN, 12));
        note.setForeground(new Color(185, 196, 234));
        choices.add(note);
        root.add(choices, BorderLayout.CENTER);

        JButton done = new JButton("DONE");
        done.setBackground(new Color(42, 205, 174));
        done.setForeground(new Color(10, 14, 30));
        done.addActionListener(e -> dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(done);
        root.add(footer, BorderLayout.SOUTH);
    }

    private JRadioButton choice(String text, QuestionSettings.Mode mode, ButtonGroup group, JPanel parent) {
        JRadioButton button = new JRadioButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(new Color(240, 242, 255));
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            if (!QuestionSettings.setMode(mode)) {
                JOptionPane.showMessageDialog(this, "The setting could not be saved.",
                        "Settings", JOptionPane.ERROR_MESSAGE);
            }
        });
        group.add(button);
        parent.add(button);
        parent.add(Box.createVerticalStrut(11));
        return button;
    }
}
