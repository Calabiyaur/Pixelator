package com.calabi.pixelator.ui.control;

import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import com.calabi.pixelator.config.Images;
import com.calabi.pixelator.view.dialog.ColorDialog;

public class BasicColorField extends BasicControl<Color> {

    private ColorField colorField;
    private ImageButton colorButton;

    public BasicColorField(String title, Color value) {
        this(title, null, value);
    }

    public BasicColorField(String title, String tail, Color value) {
        super(title, tail, value);

        colorButton = new ImageButton(Images.CHOOSE_COLOR);
        colorButton.getStyleClass().add("tight-button");
        colorButton.setOnAction(e -> ColorDialog.chooseColor(getValue(), this::setValue));
        HBox.setMargin(colorButton, new Insets(3));
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
