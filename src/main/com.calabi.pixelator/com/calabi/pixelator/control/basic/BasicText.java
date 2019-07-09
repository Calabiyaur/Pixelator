package com.calabi.pixelator.control.basic;

import javafx.scene.control.Control;
import javafx.scene.control.Label;

public class BasicText extends BasicControl<String> {

    private Label text;

    public BasicText(String title, String value) {
        this(title, null, value);
    }

    public BasicText(String title, String tail, String value) {
        super(title, tail, value);

        this.valueProperty().bindBidirectional(text.textProperty());
    }

    @Override
    public Control createControl() {
        text = new Label();
        return text;
    }

}
