package com.calabi.pixelator.view.undo;

public interface Undoable {

    void undo();

    void redo();

    boolean isEmpty();

    Undoable copy();
}
