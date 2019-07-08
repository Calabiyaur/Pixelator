package com.calabi.pixelator.control.basic;

import javafx.beans.property.Property;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

public class BasicTextField extends BasicControl<String> {

    private TextField textField;

    public BasicTextField(String title, String tail, String value) {
        super(title, tail, value);
    }

    public BasicTextField(String title, String value) {
        super(title, value);
    }

    public BasicTextField(String title) {
        super(title, "");
    }

    @Override
    public Control createControl() {
        textField = new TextField();
        return textField;
    }

    @Override
    public Property<String> valueProperty() {
        return textField.textProperty();
    }

}
