package main.java.control.basic;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;

import main.java.res.Images;

public class ToggleImageButton extends ToggleButton {

    public ToggleImageButton(Images image) {
        super("", new ImageView(image.getImage()));
    }

    public ToggleImageButton(ToggleGroup tg, Images image) {
        super("", null);
        setGraphic(new ImageView(image.getImage()));
        setToggleGroup(tg);
    }

    public ToggleImageButton(ToggleGroup tg, String text) {
        super(text, null);
        setToggleGroup(tg);
    }
}
