package ballboy.model;

public class CaretakerOneLevel {

    private MementoOneLevel memento;

    public void storeLevelMemento(MementoOneLevel memento) {
        this.memento = memento;
    }

    public MementoOneLevel uploadLevelMemento() {
        return this.memento;
    }
}
