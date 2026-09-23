public class Blind {
    int level = 1;
    int targetScore = 300;

    public void nextLevel() {
        level++;
        targetScore = (int) (targetScore * 1.5);
    }

    public void reset() {
        level = 1;
        targetScore = 300;
    }
}
