package main.java.standard.control.basic;

import javafx.beans.property.Property;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;

import main.java.res.Images;
import main.java.standard.control.ColorField;
import main.java.standard.control.ImageButton;
import main.java.view.dialog.ColorDialog;

public class BasicColorField extends BasicControl<Color> {

    private ColorField colorField;
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
        colorField = new ColorField();
        return colorField;
    }

    @Override
    public Color getValue() {
        return colorField.getColor();
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
