import java.io.Serializable;

public class Hat implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    String rarity;
    String description;
    int cost;
    int attackBonus;
    int attackMultiplier;
    int defenseBonus;
    int defenseMultiplier;
    int maxHpBonus;
    int battleStartHeal;
    int victoryHeal;
    int goldBonus;
    int extraHands;
    int perCardBonus;
    boolean revive;

    public Hat(String name, String rarity, String description, int cost, int attackBonus, int attackMultiplier,
               int defenseBonus, int defenseMultiplier, int maxHpBonus, int battleStartHeal, int victoryHeal,
               int goldBonus, int extraHands, int perCardBonus, boolean revive) {
        this.name = name;
        this.rarity = rarity;
        this.description = description;
        this.cost = cost;
        this.attackBonus = attackBonus;
        this.attackMultiplier = attackMultiplier;
        this.defenseBonus = defenseBonus;
        this.defenseMultiplier = defenseMultiplier;
        this.maxHpBonus = maxHpBonus;
        this.battleStartHeal = battleStartHeal;
        this.victoryHeal = victoryHeal;
        this.goldBonus = goldBonus;
        this.extraHands = extraHands;
        this.perCardBonus = perCardBonus;
        this.revive = revive;
    }
}
