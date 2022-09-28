package com.calabi.pixelator.util.meta;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * A compound boolean property whose value is TRUE if at least one of its children's values is TRUE.
 */
public class CompoundBooleanProperty extends SimpleBooleanProperty {

    private final ObservableSet<ObservableBooleanValue> children = FXCollections.observableSet();
    private final IntegerProperty trueAmount = new SimpleIntegerProperty(0);

    private final ChangeListener<Boolean> childValueListener = (ov, o, n) -> {
        if (n) {
            trueAmount.set(trueAmount.get() + 1);
        } else {
            trueAmount.set(trueAmount.get() - 1);
        }
    };

    public CompoundBooleanProperty() {
        this(false);
    }

    public CompoundBooleanProperty(boolean initialValue) {
        super(initialValue);
        trueAmount.addListener((ov, o, n) -> set(n.intValue() > 0));
    }

    public void add(ObservableBooleanValue child) {
        children.add(child);
        child.addListener(childValueListener);
    }

    public void remove(ObservableBooleanValue child) {
        children.remove(child);
        child.removeListener(childValueListener);
    }

}
