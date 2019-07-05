package com.calabi.pixelator.control.basic;

import javafx.beans.property.Property;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.view.dialog.ColorDialog;

public class BasicColorField extends BasicControl<Color> {

    private main.pixelator.control.basic.ColorField colorField;
    private ImageButton colorButton;

    public BasicColorField(String title, String tail, Color value) {
        super(title, tail, value);
        init();
    }

    public BasicColorField(String title, Color value) {
        super(title, value);
        init();
    }

    private void init() {
        colorButton = new ImageButton(Images.CHOOSE_COLOR);
        colorButton.setOnAction(e -> ColorDialog.chooseColor(getValue(), color -> setValue(color)));
        addControl(colorButton, 2);
    }

    @Override
    public Control createControl() {
        colorField = new main.pixelator.control.basic.ColorField();
        return colorField;
    }

    @Override
    public void setValue(Color value) {
        if (value != null) {
            colorField.setColor(value);
        }
    }

    @Override public Property<Color> valueProperty() {
        return colorField.colorProperty();
    }
}
