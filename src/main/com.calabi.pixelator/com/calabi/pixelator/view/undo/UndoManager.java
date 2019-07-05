package com.calabi.pixelator.view.undo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class UndoManager {

    private ListProperty<Undoable> changeList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private IntegerProperty position = new SimpleIntegerProperty(-1);
    private BooleanProperty undoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty redoEnabled = new SimpleBooleanProperty(false);

    public UndoManager() {
        undoEnabled.bind(position.greaterThanOrEqualTo(0).and(changeList.emptyProperty().not()));
        redoEnabled.bind(changeList.sizeProperty().greaterThan(0)
                .and(position.add(1).lessThan(changeList.sizeProperty())));
    }

    public void add(Undoable undoable) {
        // return if nothing changed
        if (undoable.isEmpty()) {
            return;
        }

        // delete old future progress
        changeList.remove(position.get() + 1, changeList.getSize());

        // add new future progress
        changeList.add(undoable.clone());
        position.set(position.get() + 1);
    }

    public void undo() {
        if (position.get() < 0 || changeList.isEmpty()) {
            throw new IllegalStateException("Undo should be impossible, there is no progress to be undone! "
                    + "(Progress: " + (position.get() + 1) + " / " + changeList.size() + ")");
        }

        Undoable undoable = changeList.get(position.get());
        undoable.undo();

        position.set(position.get() - 1);
    }

    public void redo() {
        if (changeList.isEmpty() || position.get() + 1 >= changeList.size()) {
            throw new IllegalStateException("Redo should be impossible, there is no progress to be redone! "
                    + "(Progress: " + (position.get() + 1) + " / " + changeList.size() + ")");
        }

        Undoable undoable = changeList.get(position.get() + 1);
        undoable.redo();

        position.set(position.get() + 1);
    }

    public BooleanProperty undoEnabledProperty() {
        return undoEnabled;
    }

    public BooleanProperty redoEnabledProperty() {
        return redoEnabled;
    }

    public int getPosition() {
        return position.get();
    }

    public void remove(int start, int end) {
        changeList.remove(start, end);
    }

}
