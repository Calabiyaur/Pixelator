package com.calabi.pixelator.ui.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class ColorField extends Button {

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public ColorField(Color color) {
        setPrefWidth(200);
        getStyleClass().add("color-field");

        this.color.addListener((ov, o, n) -> setStyle("-px-custom-background: " + n.toString().replace("0x", "#")));
        this.color.set(color);
    }

    public ColorField() {
        this(Color.BLACK);
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }
}
