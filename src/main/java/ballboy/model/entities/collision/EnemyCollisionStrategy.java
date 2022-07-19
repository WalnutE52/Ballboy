package ballboy.model.entities.collision;

import ballboy.model.Entity;
import ballboy.model.Level;

/**
 * Collision logic for enemies.
 */
public class EnemyCollisionStrategy implements CollisionStrategy {
    private final Level level;
    private final String color;

    public EnemyCollisionStrategy(Level level, String color) {
        this.level = level;
        this.color = color;
    }

    @Override
    public void collideWith(Entity enemy, Entity hitEntity) {
        if (level.isHero(hitEntity) && level.getCheatMode() == false) {
            level.resetHero();
        }
    }

    public String getColor() {
        return this.color;
    }
}
