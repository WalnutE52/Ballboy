package ballboy.model.entities.behaviour;

import java.util.ArrayList;
import java.util.List;

import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.utilities.Vector2D;

public class SquareCatBehaviourStrategy implements BehaviourStrategy {

    private final Level level;
    private final double speed;
    private final double side;
    private final double radius;
    private double passedLength;

    private final List<String> positions;
    private String curPosition;
    private int positionIndex;

    public SquareCatBehaviourStrategy(Level level, double speed, double radius) {
        this.level = level;
        this.side = 2 * radius;
        this.speed = speed;
        this.passedLength = 0;
        this.radius = radius;
        this.positions = new ArrayList<>();
        this.positions.add("RIGHT");
        this.positions.add("UP");
        this.positions.add("LEFT");
        this.positions.add("DOWN");
        this.positionIndex = 0;
        this.curPosition = this.positions.get(positionIndex);

    }

    public boolean isExceeded() {
        if (this.passedLength - this.side > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void behave(DynamicEntity entity, double frameDurationMilli) {

        this.passedLength += this.speed / 1000 * frameDurationMilli;

        double rightStartPointX = level.getHeroX() + 2 / level.getHeroWidth() + this.radius;
        double rightStartPointY = level.getHeroY() + 2 / level.getHeroHeight() + this.radius;

        double upStartPointX = level.getHeroX() + 2 / level.getHeroWidth() + this.radius;
        double upStartPointY = level.getHeroY() + 2 / level.getHeroHeight() - this.radius;

        double leftStartPointX = level.getHeroX() + 2 / level.getHeroWidth() - this.radius;
        double leftStartPointY = level.getHeroY() + 2 / level.getHeroHeight() - this.radius;

        double downStartPointX = level.getHeroX() + 2 / level.getHeroWidth() - this.radius;
        double downStartPointY = level.getHeroY() + 2 / level.getHeroHeight() + this.radius;

        while (isExceeded()) {
            this.passedLength -= this.side;
            this.positionIndex += 1;
            if (this.positionIndex > this.positions.size() - 1) {
                this.positionIndex = 0;

            }
            this.curPosition = this.positions.get(positionIndex);

        }

        if (curPosition.equals("RIGHT")) {
            double catX = rightStartPointX;
            double catY = rightStartPointY - this.passedLength;
            Vector2D newVector = new Vector2D(catX, catY);
            entity.setPosition(newVector);
        } else if (curPosition.equals("UP")) {
            double catX = upStartPointX - this.passedLength;
            double catY = upStartPointY;
            Vector2D newVector = new Vector2D(catX, catY);
            entity.setPosition(newVector);
        } else if (curPosition.equals("LEFT")) {
            double catX = leftStartPointX;
            double catY = leftStartPointY + this.passedLength;
            Vector2D newVector = new Vector2D(catX, catY);
            entity.setPosition(newVector);
        } else if (curPosition.equals("DOWN")) {
            double catX = downStartPointX + this.passedLength;
            double catY = downStartPointY;
            Vector2D newVector = new Vector2D(catX, catY);
            entity.setPosition(newVector);
        }

    }

}