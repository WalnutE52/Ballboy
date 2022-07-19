package ballboy.model.levels;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.MementoOneLevel;
import ballboy.model.entities.ControllableDynamicEntity;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.StaticEntity;
import ballboy.model.entities.collision.EnemyCollisionStrategy;
import ballboy.model.entities.utilities.Vector2D;
import ballboy.model.factories.EntityFactory;
import ballboy.model.observer.CurrentLevelObserver;
import ballboy.model.observer.Observer;
// import ballboy.model.observers.LevelScoreObserver;
// import ballboy.model.observers.Observer;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.stream.FactoryConfigurationError;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Level logic, with abstract factor methods.
 */
public class LevelImpl implements Level {

    private List<Entity> entities = new ArrayList<>();
    private final PhysicsEngine engine;
    private final EntityFactory entityFactory;
    private ControllableDynamicEntity<DynamicEntity> hero;
    private Entity finish;
    private double levelHeight;
    private double levelWidth;
    private double levelGravity;
    private double floorHeight;
    private Color floorColor;

    private final double frameDurationMilli;

    //
    private boolean cheatMode;
    private boolean isEnded;
    private Entity squareCat;
    private int blueScore;
    private int redScore;
    private int greenScore;
    private List<Observer> observers;

    /**
     * A callback queue for post-update jobs. This is specifically useful for
     * scheduling jobs mid-update that require the level to be in a valid state.
     */
    private final Queue<Runnable> afterUpdateJobQueue = new ArrayDeque<>();

    public LevelImpl(JSONObject levelConfiguration, PhysicsEngine engine, EntityFactory entityFactory,
            double frameDurationMilli) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.frameDurationMilli = frameDurationMilli;
        initLevel(levelConfiguration);
    }

    /**
     * Instantiates a level from the level configuration.
     *
     * @param levelConfiguration The configuration for the level.
     */
    private void initLevel(JSONObject levelConfiguration) {
        this.levelWidth = ((Number) levelConfiguration.get("levelWidth")).doubleValue();
        this.levelHeight = ((Number) levelConfiguration.get("levelHeight")).doubleValue();
        this.levelGravity = ((Number) levelConfiguration.get("levelGravity")).doubleValue();

        JSONObject floorJson = (JSONObject) levelConfiguration.get("floor");
        this.floorHeight = ((Number) floorJson.get("height")).doubleValue();
        String floorColorWeb = (String) floorJson.get("color");
        this.floorColor = Color.web(floorColorWeb);

        JSONArray generalEntities = (JSONArray) levelConfiguration.get("genericEntities");
        for (Object o : generalEntities) {
            this.entities.add(entityFactory.createEntity(this, (JSONObject) o));
        }

        JSONObject heroConfig = (JSONObject) levelConfiguration.get("hero");
        double maxVelX = ((Number) levelConfiguration.get("maxHeroVelocityX")).doubleValue();

        Object hero = entityFactory.createEntity(this, heroConfig);
        if (!(hero instanceof DynamicEntity)) {
            throw new ConfigurationParseException("hero must be a dynamic entity");
        }
        DynamicEntity dynamicHero = (DynamicEntity) hero;
        Vector2D heroStartingPosition = dynamicHero.getPosition();
        this.hero = new ControllableDynamicEntity<>(dynamicHero, heroStartingPosition, maxVelX, floorHeight,
                levelGravity);
        this.entities.add(this.hero);

        JSONObject catConfig = (JSONObject) levelConfiguration.get("cat");
        this.squareCat = entityFactory.createEntity(this, catConfig);
        this.entities.add(this.squareCat);

        JSONObject finishConfig = (JSONObject) levelConfiguration.get("finish");
        this.finish = entityFactory.createEntity(this, finishConfig);
        this.entities.add(finish);

        //
        this.cheatMode = false;
        this.isEnded = false;
        this.blueScore = 0;
        this.redScore = 0;
        this.greenScore = 0;
        this.observers = new ArrayList<>();
    }

    @Override
    public boolean getCheatMode() {
        return this.cheatMode;
    }

    @Override
    public void cheatMode() {
        this.cheatMode = !this.cheatMode;
    }

    @Override
    public boolean ifEnd() {
        return this.isEnded;
    }

    @Override
    public void end() {
        this.isEnded = true;
    }

    @Override
    public void killEnemy(DynamicEntity a) {
        String enemyColor = null;
        if (a.getCollisionStrategy() instanceof EnemyCollisionStrategy) {

            enemyColor = ((EnemyCollisionStrategy) a.getCollisionStrategy()).getColor();
        }
        if (enemyColor.equals("Red")) {
            this.redScore += 1;
        } else if (enemyColor.equals("Blue")) {
            this.blueScore += 1;
        } else {
            this.greenScore += 1;
        }
        this.entities.remove(a);

        this.notifyAllObservers();
    }

    @Override
    public int getBlueScore() {
        return this.blueScore;
    }

    @Override
    public int getRedScore() {
        return this.redScore;
    }

    @Override
    public int getGreenScore() {
        return this.greenScore;
    }

    @Override
    public int getTotalScore() {
        return this.blueScore + this.redScore + this.greenScore;
    }

    public boolean ifSquareCat(Entity a) {
        if (a instanceof DynamicEntity) {
            DynamicEntity cat = (DynamicEntity) a;
            if (cat == this.squareCat) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public boolean ifEnemy(Entity a) {
        if (a instanceof DynamicEntity) {
            DynamicEntity da = (DynamicEntity) a;
            if (!this.isHero(da) && da.getLayer() == Entity.Layer.FOREGROUND) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void notifyAllObservers() {
        for (Observer o : this.observers) {
            o.update();
        }
    }

    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    private List<DynamicEntity> getDynamicEntities() {
        return entities.stream().filter(e -> e instanceof DynamicEntity).map(e -> (DynamicEntity) e)
                .collect(Collectors.toList());
    }

    private List<StaticEntity> getStaticEntities() {
        return entities.stream().filter(e -> e instanceof StaticEntity).map(e -> (StaticEntity) e)
                .collect(Collectors.toList());
    }

    @Override
    public double getLevelHeight() {
        return this.levelHeight;
    }

    @Override
    public double getLevelWidth() {
        return this.levelWidth;
    }

    @Override
    public double getHeroHeight() {
        return hero.getHeight();
    }

    @Override
    public double getHeroWidth() {
        return hero.getWidth();
    }

    @Override
    public double getFloorHeight() {
        return floorHeight;
    }

    @Override
    public Color getFloorColor() {
        return floorColor;
    }

    @Override
    public double getGravity() {
        return levelGravity;
    }

    @Override
    public void update() {
        List<DynamicEntity> dynamicEntities = getDynamicEntities();

        dynamicEntities.stream().forEach(e -> {
            e.update(frameDurationMilli, levelGravity);
        });

        for (int i = 0; i < dynamicEntities.size(); ++i) {
            DynamicEntity dynamicEntityA = dynamicEntities.get(i);

            for (int j = i + 1; j < dynamicEntities.size(); ++j) {
                DynamicEntity dynamicEntityB = dynamicEntities.get(j);

                if (dynamicEntityA.collidesWith(dynamicEntityB)) {
                    dynamicEntityA.collideWith(dynamicEntityB);
                    dynamicEntityB.collideWith(dynamicEntityA);
                    if (!isHero(dynamicEntityA) && !isHero(dynamicEntityB)) {
                        engine.resolveCollision(dynamicEntityA, dynamicEntityB);
                    }
                }
            }
            if (!ifSquareCat(dynamicEntityA)) {
                for (StaticEntity staticEntity : getStaticEntities()) {
                    if (dynamicEntityA.collidesWith(staticEntity)) {
                        dynamicEntityA.collideWith(staticEntity);
                        engine.resolveCollision(dynamicEntityA, staticEntity, this);
                    }
                }
            }

        }

        dynamicEntities.remove(this.squareCat);
        dynamicEntities.stream().forEach(e -> engine.enforceWorldLimits(e, this));

        afterUpdateJobQueue.forEach(j -> j.run());
        afterUpdateJobQueue.clear();

    }

    @Override
    public double getHeroX() {
        return hero.getPosition().getX();
    }

    @Override
    public double getHeroY() {
        return hero.getPosition().getY();
    }

    @Override
    public boolean boostHeight() {
        return hero.boostHeight();
    }

    @Override
    public boolean dropHeight() {
        return hero.dropHeight();
    }

    @Override
    public boolean moveLeft() {
        return hero.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return hero.moveRight();
    }

    @Override
    public boolean isHero(Entity entity) {
        return entity == hero;
    }

    @Override
    public boolean isFinish(Entity entity) {
        return this.finish == entity;
    }

    @Override
    public void resetHero() {
        afterUpdateJobQueue.add(() -> this.hero.reset());
    }

    @Override
    public void finish() {
        this.isEnded = true;
    }

    @Override
    public MementoOneLevel saveLevel() {
        // TODO Auto-generated method stub
        boolean isEndedCopy = this.isEnded;
        int redScoreCopy = this.redScore;
        int blueScoreCopy = this.blueScore;
        int greenScoreCopy = this.greenScore;
        StaticEntity finishCopy = (StaticEntity) this.finish.copy();
        Entity squareCatCopy = this.squareCat.copy();
        ControllableDynamicEntity<DynamicEntity> heroCopy = this.hero.copy();
        List<Entity> entitiesCopy = new ArrayList<>();
        for (int i = 0; i < this.entities.size(); i++) {
            Entity entity = this.entities.get(i);
            if (entity != this.hero && entity != finish && entity != squareCat) {
                entitiesCopy.add(entity.copy());
            }
        }
        entitiesCopy.add(heroCopy);
        entitiesCopy.add(finishCopy);
        entitiesCopy.add(squareCatCopy);

        MementoOneLevel mlevel = new MementoOneLevel(entitiesCopy, heroCopy, finishCopy, squareCatCopy, isEndedCopy,
                blueScoreCopy, redScoreCopy, greenScoreCopy);
        return mlevel;
    }

    @Override
    public void loadLevel(MementoOneLevel mementoOneLevel) {
        // TODO Auto-generated method stub
        boolean isEndedCopy = mementoOneLevel.getIsEnded();
        int redScoreCopy = mementoOneLevel.getRedScore();
        int blueScoreCopy = mementoOneLevel.getBlueScore();
        int greenScoreCopy = mementoOneLevel.getGreenScore();
        ControllableDynamicEntity<DynamicEntity> heroCopy = mementoOneLevel.getHero();
        List<Entity> entitiesCopy = mementoOneLevel.getAllEntities();
        Entity finishCopy = mementoOneLevel.getFinish();
        Entity squareCatCopy = mementoOneLevel.getSquareCat();
        this.isEnded = isEndedCopy;
        this.redScore = redScoreCopy;
        this.blueScore = blueScoreCopy;
        this.greenScore = greenScoreCopy;
        this.hero = heroCopy;
        this.finish = (StaticEntity) finishCopy;
        this.squareCat = squareCatCopy;
        this.entities = entitiesCopy;
        notifyAllObservers();

    }

}
