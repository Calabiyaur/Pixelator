package com.calabi.pixelator.view.undo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class UndoManager {

    private final ListProperty<Undoable> changeList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty position = new SimpleIntegerProperty(-1);
    private final BooleanProperty undoEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty redoEnabled = new SimpleBooleanProperty(false);

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

        if (undoable instanceof IndexChange) {

            // merge index changes
            addIndexChange(undoable.copy());

        } else {
            // delete old future progress
            changeList.remove(position.get() + 1, changeList.size());

            // add new future progress
            changeList.add(undoable.copy());
            position.set(position.get() + 1);
        }
    }

    public void undo() {
        if (position.get() < 0 || changeList.isEmpty()) {
            throw new IllegalStateException("Undo should be impossible, there is no progress to be undone! "
                    + "(Progress: " + (position.get() + 1) + " / " + changeList.size() + ")");
        }

        Undoable undoable = changeList.get(position.get());
        undoable.undo();

        int newPosition = position.get() - 1;

        // Remove single-use index changes
        if (undoable instanceof IndexChange) {
            Undoable redoPendant = null;
            boolean singleUse = position.get() == changeList.size() - 1
                    || (redoPendant = changeList.get(position.get() + 1)) instanceof IndexChange;
            if (singleUse) {
                changeList.remove(position.get());
                if (redoPendant != null) {
                    changeList.remove(position.get());
                }
            }
        }

        position.set(newPosition);
    }

    public void redo() {
        if (changeList.isEmpty() || position.get() + 1 >= changeList.size()) {
            throw new IllegalStateException("Redo should be impossible, there is no progress to be redone! "
                    + "(Progress: " + (position.get() + 1) + " / " + changeList.size() + ")");
        }

        Undoable undoable = changeList.get(position.get() + 1);
        undoable.redo();

        int newPosition = position.get() + 1;

        // Remove single-use index changes
        if (undoable instanceof IndexChange) {
            Undoable undoPendant = null;
            boolean singleUse = position.get() == -1
                    || (undoPendant = changeList.get(position.get())) instanceof IndexChange;
            if (singleUse) {
                changeList.remove(newPosition);
                newPosition--;
                if (undoPendant != null) {
                    changeList.remove(position.get());
                    newPosition--;
                }
            }
        }

        position.set(newPosition);
    }

    private void addIndexChange(Undoable undoable) {
        // empty changelist cannot start with index change
        if (changeList.isEmpty()) {
            return;
        }

        if (undoEnabled.get()) {
            addIndexChangeAfter(undoable);
        }

        if (redoEnabled.get()) {
            addIndexChangeBefore((IndexChange) undoable);
        }
    }

    private void addIndexChangeAfter(Undoable undoable) {
        // if the previous change was also an index change, merge
        if (changeList.get(position.get()) instanceof IndexChange) {

            IndexChange prior = (IndexChange) changeList.get(position.get());
            prior.merge((IndexChange) undoable);

            // if the resulting index change is empty, remove it
            if (prior.isEmpty()) {
                changeList.remove(position.get());
                position.set(position.get() - 1);
            }
        } else {

            // Add index change at current position
            changeList.add(position.get() + 1, undoable);
            position.set(position.get() + 1);
        }
    }

    private void addIndexChangeBefore(IndexChange undoable) {
        // if the next change is also an index change, merge
        if (changeList.get(position.get() + 1) instanceof IndexChange) {

            IndexChange next = (IndexChange) changeList.get(position.get() + 1);
            undoable.reverse().merge(next);

            // if the resulting index change is empty, remove it
            if (next.isEmpty()) {
                changeList.remove(position.get() + 1);
            }
        } else {

            // Add reversed index change before old future progress
            changeList.add(position.get() + 1, undoable.reverse());
        }
    }

    public BooleanProperty undoEnabledProperty() {
        return undoEnabled;
    }

    public BooleanProperty redoEnabledProperty() {
        return redoEnabled;
    }

}
