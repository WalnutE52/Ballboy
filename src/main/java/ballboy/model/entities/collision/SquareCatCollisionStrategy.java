package ballboy.model.entities.collision;

import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;

public class SquareCatCollisionStrategy implements CollisionStrategy {

    private final Level level;

    public SquareCatCollisionStrategy(Level level) {
        this.level = level;
    }

    @Override
    public void collideWith(Entity currentEntity, Entity hitEntity) {
        if (level.ifEnemy(hitEntity)) {
            this.level.killEnemy((DynamicEntity) hitEntity);
        }
    }

}