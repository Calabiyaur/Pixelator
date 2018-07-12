package main.java.standard.control;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import main.java.res.Images;

public class ImageButton extends Button {

    public ImageButton(Images image) {
        super("", new ImageView(image.getImage()));
        getStyleClass().setAll("tool-button");
    }
}
