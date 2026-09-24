import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

public class CombatQuestionChecks {
    public static void main(String[] args) throws IOException {
        if (QuestionSettings.getMode() == null) throw new AssertionError("Missing question setting");
        List<CombatQuestion> demo = CombatQuestion.loadQuestions(Path.of("questions.txt"));
        if (demo.size() < 10) throw new AssertionError("Demo question file is too small");
        for (CombatQuestion question : demo) {
            if (question.online || question.choices.size() != 4
                    || new HashSet<>(question.choices).size() != 4
                    || !question.choices.contains(question.answer)) {
                throw new AssertionError("Invalid file question choices");
            }
            String[] equation = question.equation.split(" ");
            int left = Integer.parseInt(equation[0]);
            int right = Integer.parseInt(equation[2]);
            int expected = switch (equation[1]) {
                case "+" -> left + right;
                case "−" -> left - right;
                case "×" -> left * right;
                default -> throw new AssertionError("Unknown operation");
            };
            if (question.answer != expected) throw new AssertionError("Wrong answer to " + question.equation);
        }
        for (int i = 0; i < 50; i++) {
            CombatQuestion question = CombatQuestion.generate(false);
            if (demo.stream().noneMatch(item -> item.prompt.equals(question.prompt))) {
                throw new AssertionError("Generated question did not come from questions.txt");
            }
        }
        Path edited = Files.createTempFile("balnotro-demo-questions", ".txt");
        try {
            Files.writeString(edited, "Test question?|1 + 2 = ?|1|2|3|4|3\n");
            if (CombatQuestion.loadQuestions(edited).getFirst().answer != 3) {
                throw new AssertionError("Edited question was not loaded");
            }
            Files.writeString(edited, "Test question?|1 + 2 = ?|1|2|3|4|4\n");
            try {
                CombatQuestion.loadQuestions(edited);
                throw new AssertionError("Incorrect answer was accepted");
            } catch (IOException expected) {
                // Bad file rows must fail before a combat question is shown.
            }
        } finally {
            Files.deleteIfExists(edited);
        }
        String sample = "{\"output\":[{\"type\":\"message\",\"content\":[{\"type\":\"output_text\","
                + "\"text\":\"A dragon has 4\\ncrystals. How many?\"}]}]}";
        if (!CombatQuestion.extractOutputText(sample).equals("A dragon has 4\ncrystals. How many?")) {
            throw new AssertionError("Response text extraction failed");
        }
        System.out.println("Demo question file, reloads, answers, and response parsing verified.");
    }
}
