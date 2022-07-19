package ballboy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the GameEngine interface. This provides a common interface
 * for the entire game.
 */
public class GameEngineImpl implements GameEngine {
    private final List<Level> allLevels;
    private Level level;
    private int levelIndex;
    private boolean endGame;
    private List<CaretakerOneLevel> caretakers;

    public GameEngineImpl(Integer levelIndex, List<Level> allLevels) {
        this.levelIndex = levelIndex;
        this.allLevels = allLevels;
        this.level = allLevels.get(levelIndex);
        this.endGame = false;
        this.caretakers = new ArrayList<>();
    }

    public Level getCurrentLevel() {
        return level;
    }

    public void startLevel() {
        // TODO: Handle when multiple levels has been implemented
        return;
    }

    public boolean boostHeight() {
        return level.boostHeight();
    }

    public boolean dropHeight() {
        return level.dropHeight();
    }

    public boolean moveLeft() {
        return level.moveLeft();
    }

    public boolean moveRight() {
        return level.moveRight();
    }

    public boolean ifNextLevel() {
        if (this.levelIndex < this.allLevels.size()) {
            return true;
        }
        return false;
    }

    public boolean ifEndGame() {
        return this.endGame;
    }

    public void tick() {
        level.update();

        //
        if (level.ifEnd()) {
            this.levelIndex += 1;
            if (this.ifNextLevel()) {
                Level next = this.allLevels.get(levelIndex);
                this.level = next;
                this.level.notifyAllObservers();
            } else {
                this.endGame = true;
            }
        }

    }

    public Memento storeGame() {

        this.caretakers = new ArrayList<>();
        for (int i = 0; i < allLevels.size(); i++) {
            CaretakerOneLevel taker = new CaretakerOneLevel();
            Level curLevel = allLevels.get(i);

            MementoOneLevel levelmMemento = curLevel.saveLevel();

            taker.storeLevelMemento(levelmMemento);
            List<Entity> a = taker.uploadLevelMemento().getAllEntities();
            Entity b = taker.uploadLevelMemento().getFinish();

            this.caretakers.add(taker);
        }
        Memento indexMemento = new Memento(this.levelIndex);

        return indexMemento;

    }

    public void loadGame(Memento m) {

        for (int i = 0; i < this.caretakers.size(); i++) {
            CaretakerOneLevel curTaker = this.caretakers.get(i);
            MementoOneLevel levelmemento = curTaker.uploadLevelMemento();

            Level curLevel = this.allLevels.get(i);
            curLevel.loadLevel(levelmemento);

        }
        int indexCopoy = m.getLevelIndex();
        this.levelIndex = indexCopoy;
        this.level = this.allLevels.get(levelIndex);
        this.storeGame();

    }

    public void cheatMode() {
        this.level.cheatMode();
    }

}