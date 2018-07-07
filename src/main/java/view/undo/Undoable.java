package main.java.view.undo;

public interface Undoable {

    void undo();

    void redo();

    boolean isEmpty();

    Undoable clone();
}
