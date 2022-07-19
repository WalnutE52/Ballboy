package ballboy.model;

public class Memento {
    private int levelIndex;

    public Memento(int levelIndex) {
        this.levelIndex = levelIndex;

    }

    public int getLevelIndex() {
        return this.levelIndex;
    }

}