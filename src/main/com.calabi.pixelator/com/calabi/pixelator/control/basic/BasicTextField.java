package com.calabi.pixelator.control.basic;

import javafx.scene.control.Control;
import javafx.scene.control.TextField;

public class BasicTextField extends BasicControl<String> {

    private TextField textField;

    public BasicTextField(String title) {
        this(title, "");
    }

    public BasicTextField(String title, String value) {
        this(title, null, value);
    }

    public BasicTextField(String title, String tail, String value) {
        super(title, tail, value);

        this.valueProperty().bindBidirectional(textField.textProperty());
    }

    @Override
    public Control createControl() {
        textField = new TextField();
        return textField;
    }

}
