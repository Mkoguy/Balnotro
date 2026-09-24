import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class CombatQuestionDialog extends JDialog {
    private final JPanel answers = new JPanel(new GridLayout(2, 2, 12, 12));
    private final JLabel prompt = new JLabel("Preparing your question...", SwingConstants.CENTER);
    private final JLabel equation = new JLabel("", SwingConstants.CENTER);
    private final JLabel feedback = new JLabel("", SwingConstants.CENTER);
    private final JButton continueButton = new JButton("CONTINUE");
    private final boolean requestedOnline;
    private SwingWorker<CombatQuestion, Void> worker;
    private boolean answeredCorrectly;
    private boolean correct;

    public static boolean ask(JFrame owner, QuestionSettings.Mode mode) {
        CombatQuestionDialog dialog = new CombatQuestionDialog(owner, mode);
        dialog.setVisible(true);
        return dialog.correct;
    }

    private CombatQuestionDialog(JFrame owner, QuestionSettings.Mode mode) {
        super(owner, "Combat Question", true);
        requestedOnline = mode == QuestionSettings.Mode.ONLINE;
        setSize(580, 360);
        setMinimumSize(new Dimension(520, 330));
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 15));
        root.setBackground(new Color(10, 14, 30));
        root.setBorder(new EmptyBorder(24, 28, 24, 28));
        setContentPane(root);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("ANSWER TO ACT");
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setForeground(new Color(255, 201, 94));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        prompt.setFont(new Font("SansSerif", Font.BOLD, 16));
        prompt.setForeground(new Color(240, 242, 255));
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        equation.setFont(new Font("SansSerif", Font.BOLD, 20));
        equation.setForeground(new Color(42, 205, 174));
        equation.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.add(title);
        heading.add(Box.createVerticalStrut(16));
        heading.add(prompt);
        heading.add(Box.createVerticalStrut(8));
        heading.add(equation);
        root.add(heading, BorderLayout.NORTH);

        answers.setOpaque(false);
        root.add(answers, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setOpaque(false);
        feedback.setFont(new Font("SansSerif", Font.PLAIN, 12));
        feedback.setForeground(new Color(185, 196, 234));
        feedback.setText("A correct answer unlocks your selected action.");
        continueButton.setBackground(new Color(42, 205, 174));
        continueButton.setEnabled(false);
        continueButton.setVisible(false);
        continueButton.addActionListener(e -> {
            correct = answeredCorrectly;
            dispose();
        });
        footer.add(feedback, BorderLayout.CENTER);
        footer.add(continueButton, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        worker = new SwingWorker<>() {
            @Override
            protected CombatQuestion doInBackground() throws Exception {
                return CombatQuestion.generate(mode == QuestionSettings.Mode.ONLINE);
            }

            @Override
            protected void done() {
                if (!isDisplayable()) return;
                try {
                    showQuestion(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    dispose();
                } catch (ExecutionException exception) {
                    String detail = exception.getCause() == null ? "Unknown error" : exception.getCause().getMessage();
                    JOptionPane.showMessageDialog(CombatQuestionDialog.this,
                            "Could not load a combat question:\n" + detail + "\nNo cards or turn were spent.",
                            "Question File Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                } catch (CancellationException exception) {
                    dispose();
                }
            }
        };
        worker.execute();
    }

    private void showQuestion(CombatQuestion question) {
        prompt.setText("<html><div style='text-align:center; width:490px'>" + escape(question.prompt) + "</div></html>");
        equation.setText(question.equation);
        feedback.setText(question.online ? "Online AI question"
                : requestedOnline ? "AI unavailable; using a local question" : "Local question");
        for (int option : question.choices) {
            JButton button = new JButton(String.valueOf(option));
            button.setFont(new Font("SansSerif", Font.BOLD, 20));
            button.setForeground(new Color(240, 242, 255));
            button.setBackground(new Color(39, 49, 82));
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                answeredCorrectly = option == question.answer;
                for (Component component : answers.getComponents()) component.setEnabled(false);
                button.setBackground(answeredCorrectly ? new Color(20, 110, 86) : new Color(126, 45, 65));
                feedback.setText(answeredCorrectly ? "Correct! Your action is ready."
                        : "Answer: " + question.answer + ". No cards or turn spent.");
                continueButton.setText(answeredCorrectly ? "ACT" : "BACK TO COMBAT");
                continueButton.setVisible(true);
                continueButton.setEnabled(true);
            });
            answers.add(button);
        }
        answers.revalidate();
        answers.repaint();
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
