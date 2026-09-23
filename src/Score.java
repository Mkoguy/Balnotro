import java.io.*;
import javax.swing.*;

public class Score {
    int current = 0;
    int target;

    Score(int target) {
        this.target = target;
    }

    void saveToFile(String pName) {
        try (FileWriter fw = new FileWriter("score.txt")) {
            fw.write("Player: " + pName + " - Score: " + current);
        } catch (IOException e) {
            System.out.println("Error saving");
            return;
        }
        JOptionPane.showMessageDialog(null, "Saved!");
    }
}
