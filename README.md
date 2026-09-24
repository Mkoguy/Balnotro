# Balnotro

A Java Swing card combat game with a free poker-hand practice mode.

## Run

Open the project in IntelliJ IDEA and run `src/Main.java`, or from the project root:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac --release 23 -encoding UTF-8 -d out src/*.java
java -cp out Main
```

## Learn while playing

Choose **Practice Lab** on the main menu, or **Practice** during a run. Each of the ten example hands asks you to identify the strongest poker combination and predict its attack damage with a hero multiplier of 1 and no hats. The game then reveals the rule and calculation. A session summary shows separate hand and math scores and which hand types to review. Practice does not alter saves, lives, gold, or combat state.

During combat, selecting cards previews the hand, attack damage, defense block, and the calculation behind each value. **How to Play** explains the rules at any time.

## Optional combat questions

Open **Settings** from the main menu or during a run to choose **Off**, **Local**, or **Online AI** questions. With questions on, each ATTACK or DEFEND asks a new four-choice math question. A correct answer performs the action. A wrong answer reveals the solution and lets you retry without spending cards or a turn. Settings persist between launches and apply to existing saves.

Local mode reads [questions.txt](questions.txt) from the project root. It includes twelve easy demo questions, and edits take effect on the next combat question without restarting. Each non-comment line has seven `|`-separated fields: `question|equation|choice 1|choice 2|choice 3|choice 4|answer`. Use `+`, `−`, or `×` in the equation, such as `2 + 3 = ?`. The game checks that the answer matches the equation and appears exactly once among four distinct choices.

Online AI uses the [OpenAI Responses API](https://developers.openai.com/api/docs/guides/text) to rephrase a question from the file as a child-friendly word problem. The file supplies the equation, answer, and four choices. Set `OPENAI_API_KEY` in the game's environment before starting Java. If the key or service is unavailable, the file question is shown as written. Online requests use the `gpt-6-luna` model and may incur API charges.

To verify that all ten practice examples match the game's poker-hand evaluator:

```powershell
javac --release 23 -encoding UTF-8 -d out src/*.java tests/*.java
java -cp out PracticeChecks
java -cp out CombatQuestionChecks
```
