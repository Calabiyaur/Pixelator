package com.calabi.pixelator.control.basic;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;

public class BasicCheckBox extends BasicControl<Boolean> {

    private CheckBox checkBox;

    public BasicCheckBox(String title) {
        this(title, false);
    }

    public BasicCheckBox(String title, boolean value) {
        this(title, null, value);
    }

    public BasicCheckBox(String title, String tail, boolean value) {
        super(title, tail, value);

        checkBox.selectedProperty().bindBidirectional(this.valueProperty());
    }

    @Override
    public Control createControl() {
        checkBox = new CheckBox();
        return checkBox;
    }

}
