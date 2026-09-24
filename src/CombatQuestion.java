import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public final class CombatQuestion {
    private static final Path QUESTION_FILE = Path.of("questions.txt");
    private static final Pattern EQUATION = Pattern.compile("(\\d+) ([+−×]) (\\d+) = \\?");
    public final String prompt;
    public final String equation;
    public final List<Integer> choices;
    public final int answer;
    public final boolean online;

    private CombatQuestion(String prompt, String equation, List<Integer> choices, int answer, boolean online) {
        this.prompt = prompt;
        this.equation = equation;
        this.choices = List.copyOf(choices);
        this.answer = answer;
        this.online = online;
    }

    public static CombatQuestion generate(boolean requestOnline) throws IOException {
        List<CombatQuestion> questions = loadQuestions(QUESTION_FILE);
        CombatQuestion selected = questions.get(ThreadLocalRandom.current().nextInt(questions.size()));
        if (requestOnline) {
            try {
                Matcher equation = EQUATION.matcher(selected.equation);
                if (!equation.matches()) return selected;
                String prompt = onlinePrompt(Integer.parseInt(equation.group(1)),
                        Integer.parseInt(equation.group(3)), equation.group(2));
                return new CombatQuestion(prompt, selected.equation, selected.choices, selected.answer, true);
            } catch (IOException | InterruptedException | IllegalArgumentException exception) {
                if (exception instanceof InterruptedException) Thread.currentThread().interrupt();
            }
        }
        return selected;
    }

    static List<CombatQuestion> loadQuestions(Path path) throws IOException {
        List<CombatQuestion> questions = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] fields = line.split("\\|", -1);
            if (fields.length != 7) throw new IOException("questions.txt line " + (lineNumber + 1) + " needs 7 fields");
            String prompt = fields[0].trim();
            String equation = fields[1].trim();
            Matcher match = EQUATION.matcher(equation);
            if (prompt.isEmpty() || prompt.length() > 240 || !match.matches()) {
                throw new IOException("Invalid question on line " + (lineNumber + 1));
            }
            try {
                List<Integer> choices = List.of(Integer.parseInt(fields[2].trim()),
                        Integer.parseInt(fields[3].trim()), Integer.parseInt(fields[4].trim()),
                        Integer.parseInt(fields[5].trim()));
                int answer = Integer.parseInt(fields[6].trim());
                int left = Integer.parseInt(match.group(1));
                int right = Integer.parseInt(match.group(3));
                int expected = switch (match.group(2)) {
                    case "+" -> left + right;
                    case "−" -> left - right;
                    case "×" -> left * right;
                    default -> throw new IllegalArgumentException();
                };
                if (answer != expected || new HashSet<>(choices).size() != 4
                        || !choices.contains(answer) || choices.stream().anyMatch(value -> value < 0)) {
                    throw new IllegalArgumentException();
                }
                questions.add(new CombatQuestion(prompt, equation, choices, answer, false));
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid choices or answer on line " + (lineNumber + 1), exception);
            }
        }
        if (questions.isEmpty()) throw new IOException("questions.txt has no questions");
        return questions;
    }

    private static String onlinePrompt(int a, int b, String symbol) throws IOException, InterruptedException {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) throw new IOException("OPENAI_API_KEY is missing");
        String instructions = "Write one short, child-friendly fantasy word problem for the exact arithmetic "
                + "in the input. Use the two numbers and the operation exactly. End with a question mark. "
                + "Return only the question sentence, with no answer or choices.";
        String input = a + " " + symbol + " " + b;
        String body = "{\"model\":\"gpt-6-luna\",\"reasoning\":{\"effort\":\"none\"},"
                + "\"max_output_tokens\":120,\"instructions\":\"" + jsonEscape(instructions)
                + "\",\"input\":\"" + jsonEscape(input) + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/responses"))
                .timeout(Duration.ofSeconds(18))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Question service returned " + response.statusCode());
        String text = extractOutputText(response.body()).trim().replaceAll("\\s+", " ");
        if (text.length() < 12 || text.length() > 240 || !text.endsWith("?")
                || !Pattern.compile("\\b" + a + "\\b").matcher(text).find()
                || !Pattern.compile("\\b" + b + "\\b").matcher(text).find()) {
            throw new IOException("Question service returned an invalid question");
        }
        return text;
    }

    private static String jsonEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    // Read text only from an output_text content block in the Responses API result.
    static String extractOutputText(String json) throws IOException {
        int type = json.indexOf("\"output_text\"");
        if (type < 0) throw new IOException("No question text in response");
        int key = json.indexOf("\"text\"", type);
        if (key < 0) throw new IOException("No question text in response");
        int colon = json.indexOf(':', key + 6);
        int quote = json.indexOf('"', colon + 1);
        if (colon < 0 || quote < 0) throw new IOException("Invalid response text");
        StringBuilder value = new StringBuilder();
        for (int i = quote + 1; i < json.length(); i++) {
            char character = json.charAt(i);
            if (character == '"') return value.toString();
            if (character != '\\') {
                value.append(character);
                continue;
            }
            if (++i >= json.length()) break;
            char escaped = json.charAt(i);
            switch (escaped) {
                case '"', '\\', '/' -> value.append(escaped);
                case 'n' -> value.append('\n');
                case 'r' -> value.append('\r');
                case 't' -> value.append('\t');
                case 'u' -> {
                    if (i + 4 >= json.length()) throw new IOException("Invalid Unicode escape");
                    try {
                        value.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
                    } catch (NumberFormatException exception) {
                        throw new IOException("Invalid Unicode escape", exception);
                    }
                    i += 4;
                }
                default -> throw new IOException("Invalid text escape");
            }
        }
        throw new IOException("Unterminated response text");
    }
}
