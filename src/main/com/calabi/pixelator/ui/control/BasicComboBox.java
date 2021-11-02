package com.calabi.pixelator.ui.control;

import java.util.Arrays;
import java.util.Collection;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;

public class BasicComboBox<T> extends BasicControl<T> {

    private ComboBox<T> comboBox;

    public BasicComboBox(String title, T[] items, T value) {
        this(title, Arrays.asList(items), value);
    }

    public BasicComboBox(String title, Collection<T> items, T value) {
        this(title, null, items, value);
    }

    public BasicComboBox(String title, String tail, Collection<T> items, T value) {
        super(title, tail, value);

        comboBox.getItems().setAll(items);
        this.valueProperty().bindBidirectional(comboBox.valueProperty());
    }

    @Override
    public Control createControl() {
        comboBox = new ComboBox<T>();
        comboBox.setMinWidth(100);
        return comboBox;
    }

}
