import java.io.Serializable;

public class LevelManager implements Serializable {
    private static final long serialVersionUID = 1L;
    int level = 1;
    private final String[] enemyNames = {
            "Mossback Goblin", "Ashen Warden", "Clockwork Knight", "Moonlit Witch", "Void Marauder",
            "Crystal Hydra", "Grave King", "Storm Colossus", "Star Eater", "The Hatless One"
    };

    public Enemy generateNextEnemy() {
        int hp = (int) Math.round(90 * Math.pow(1.34, level - 1));
        int attack = 9 + (level * 3);
        int reward = 22 + (level * 8);
        String name = enemyNames[(level - 1) % enemyNames.length];
        return new Enemy(name, hp, attack, reward);
    }

    public void advanceLevel(Player player, Enemy defeatedEnemy) {
        player.gold += defeatedEnemy.reward + player.getGoldBonus();
        player.heal(player.getVictoryHeal());
        player.baseMult++;
        level++;
    }

    public void reset() {
        level = 1;
    }
}
