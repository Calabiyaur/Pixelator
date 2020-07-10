package com.calabi.pixelator.ui.control;

import javafx.scene.control.Control;
import javafx.scene.paint.Color;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.view.dialog.ColorDialog;

public class BasicColorField extends BasicControl<Color> {

    private ColorField colorField;
    private ImageButton colorButton;

    public BasicColorField(String title, Color value) {
        this(title, null, value);
    }

    public BasicColorField(String title, String tail, Color value) {
        super(title, tail, value);
        init();
    }

    private void init() {
        colorButton = new ImageButton(Images.CHOOSE_COLOR);
        colorButton.setOnAction(e -> ColorDialog.chooseColor(getValue(), color -> setValue(color)));
        addControl(colorButton, 1);

        colorField.colorProperty().bindBidirectional(this.valueProperty());
        this.valueProperty().addListener((ov, o, n) -> {
            if (n == null && o != null) {
                setValue(o);
            }
        });
    }

    @Override
    public Control createControl() {
        colorField = new ColorField();
        return colorField;
    }

}
