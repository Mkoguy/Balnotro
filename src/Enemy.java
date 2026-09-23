import java.io.Serializable;

public class Enemy implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    int hp;
    int maxHp;
    int baseAttack;
    int turnCount = 0;
    int reward;

    public Enemy(String name, int maxHp, int baseAttack, int reward) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.baseAttack = baseAttack;
        this.reward = reward;
    }

    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
    }

    public int getNextAttack() {
        return baseAttack + (turnCount % 3 == 2 ? Math.max(4, baseAttack / 2) : 0);
    }

    public String getIntent() {
        if (turnCount % 3 == 2) return "Fury Strike " + getNextAttack();
        return "Strike " + getNextAttack();
    }

    public void advanceTurn() {
        turnCount++;
    }

    public boolean isDead() {
        return hp <= 0;
    }
}
