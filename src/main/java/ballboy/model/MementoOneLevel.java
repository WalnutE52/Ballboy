package ballboy.model;

import java.util.List;

import ballboy.model.entities.ControllableDynamicEntity;
import ballboy.model.entities.DynamicEntity;

public class MementoOneLevel {

    private List<Entity> entities;
    private ControllableDynamicEntity<DynamicEntity> hero;
    private Entity finish;
    private Entity squareCat;
    private boolean isEnded;
    private int blueScore;
    private int redScore;
    private int greenScore;

    public MementoOneLevel(List<Entity> entities, ControllableDynamicEntity<DynamicEntity> hero, Entity finish,
            Entity squareCat, boolean isEnded, int blueScore, int redScore, int greenScore) {
        this.entities = entities;
        this.hero = hero;
        this.finish = finish;
        this.squareCat = squareCat;
        this.isEnded = isEnded;
        this.blueScore = blueScore;
        this.redScore = redScore;
        this.greenScore = greenScore;
    }

    public List<Entity> getAllEntities() {
        return this.entities;
    }

    public ControllableDynamicEntity<DynamicEntity> getHero() {
        return this.hero;
    }

    public Entity getFinish() {
        return this.finish;
    }

    public Entity getSquareCat() {
        return this.squareCat;
    }

    public boolean getIsEnded() {
        return this.isEnded;
    }

    public int getRedScore() {
        return this.redScore;
    }

    public int getBlueScore() {
        return this.blueScore;
    }

    public int getGreenScore() {
        return this.greenScore;
    }
}
