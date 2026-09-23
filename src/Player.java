import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    int maxHp = 100;
    int hp = 100;
    int block = 0;
    int hands = 41;
    int discards = 31;
    int baseMult = 1;
    int gold = 100;
    int lives = 3;
    boolean reviveUsed = false;
    List<Hat> hats = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public void resetResources() {
        hands = 4 + getExtraHands();
        discards = 3;
        block = 0;
        heal(getBattleStartHeal());
    }

    public void equip(Hat hat) {
        hats.add(hat);
        maxHp += hat.maxHpBonus;
        heal(hat.maxHpBonus);
    }

    public int getAttackMultiplier() {
        int multiplier = baseMult;
        for (Hat hat : hats) {
            if (hat.attackMultiplier > 1) multiplier *= hat.attackMultiplier;
        }
        return multiplier;
    }

    public int getAttackBonus() {
        int bonus = 0;
        for (Hat hat : hats) bonus += hat.attackBonus;
        return bonus;
    }

    public int getPerCardBonus() {
        int bonus = 0;
        for (Hat hat : hats) bonus += hat.perCardBonus;
        return bonus;
    }

    public int getDefenseBonus() {
        int bonus = 0;
        for (Hat hat : hats) bonus += hat.defenseBonus;
        return bonus;
    }

    public int getDefenseMultiplier() {
        int multiplier = 1;
        for (Hat hat : hats) {
            if (hat.defenseMultiplier > 1) multiplier *= hat.defenseMultiplier;
        }
        return multiplier;
    }

    public int getBattleStartHeal() {
        int heal = 0;
        for (Hat hat : hats) heal += hat.battleStartHeal;
        return heal;
    }

    public int getVictoryHeal() {
        int heal = 0;
        for (Hat hat : hats) heal += hat.victoryHeal;
        return heal;
    }

    public int getGoldBonus() {
        int bonus = 0;
        for (Hat hat : hats) bonus += hat.goldBonus;
        return bonus;
    }

    public int getExtraHands() {
        int bonus = 0;
        for (Hat hat : hats) bonus += hat.extraHands;
        return bonus;
    }

    public boolean canRevive() {
        if (reviveUsed) return false;
        for (Hat hat : hats) if (hat.revive) return true;
        return false;
    }

    public void gainBlock(int amount) {
        block += Math.max(0, amount);
    }

    public int takeDamage(int amount) {
        int damage = Math.max(0, amount - block);
        block = Math.max(0, block - amount);
        hp -= damage;
        if (hp <= 0 && canRevive()) {
            reviveUsed = true;
            hp = Math.max(1, maxHp / 3);
        } else if (hp < 0) {
            hp = 0;
        }
        return damage;
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + Math.max(0, amount));
    }

    public boolean isDefeated() {
        return hp <= 0;
    }
}
