package com.calabi.pixelator.control.basic;

import javafx.beans.property.Property;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

public class BasicText extends BasicControl<String> {

    private Label text;

    public BasicText(String title, String tail, String value) {
        super(title, tail, value);
    }

    public BasicText(String title, String tail, Integer value) {
        super(title, tail, value.toString());
    }

    public BasicText(String title, String value) {
        super(title, value);
    }

    public BasicText(String title, Integer value) {
        super(title, value.toString());
    }

    @Override
    public Control createControl() {
        text = new Label();
        return text;
    }

    @Override public Property<String> valueProperty() {
        return text.textProperty();
    }

    public Integer getIntValue() {
        try {
            return Integer.parseInt(getValue());
        } catch (NumberFormatException e) {
            System.out.println(getValue() + " is not a valid number.");
            return null;
        }
    }

}
