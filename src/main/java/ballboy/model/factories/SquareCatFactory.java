package ballboy.model.factories;

import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.DynamicEntityImpl;
import ballboy.model.entities.behaviour.PassiveEntityBehaviourStrategy;
import ballboy.model.entities.behaviour.SquareCatBehaviourStrategy;
import ballboy.model.entities.collision.BallboyCollisionStrategy;
import ballboy.model.entities.collision.SquareCatCollisionStrategy;
import ballboy.model.entities.utilities.*;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.util.List;

public class SquareCatFactory implements EntityFactory {

    @Override
    public Entity createEntity(Level level, JSONObject catConfig) {
        DynamicEntity ballboy = null;
        List<Entity> allEntities = level.getEntities();
        String imageName = (String) catConfig.get("image");
        Image image = new Image(imageName);

        double height = ((Number) catConfig.get("size")).doubleValue();
        double width = height * image.getWidth() / image.getHeight();

        double speed = ((Number) catConfig.get("speed")).doubleValue();
        double radius = ((Number) catConfig.get("radius")).doubleValue();

        Vector2D startingPosition = new Vector2D(0, 0);

        KinematicState kinematicState = new KinematicStateImpl.KinematicStateBuilder().setPosition(startingPosition)
                .build();

        AxisAlignedBoundingBox volume = new AxisAlignedBoundingBoxImpl(startingPosition, height, width);

        return new DynamicEntityImpl(kinematicState, volume, Entity.Layer.EFFECT, new Image(imageName),
                new SquareCatCollisionStrategy(level), new SquareCatBehaviourStrategy(level, speed, radius));
    }
}
