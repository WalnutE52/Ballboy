package ballboy.model;

public class Caretaker {
    private Memento memento = null;

    public void storeMemento(Memento m) {
        this.memento = m;
    }

    public Memento uploadMemento() {
        return this.memento;
    }
}
