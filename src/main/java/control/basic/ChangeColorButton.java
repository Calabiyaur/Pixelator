package main.java.control.basic;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;

public class ChangeColorButton extends ComboBox<Color> {

    private final Color leftColor;

    public ChangeColorButton(Color leftColor, Collection<Color> rightColors) {
        super(FXCollections.observableArrayList(rightColors));
        this.leftColor = leftColor;
        setValue(leftColor);
    }

}
