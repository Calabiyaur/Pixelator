package com.calabi.pixelator.ui.control;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.start.MainScene;

public class ImageButton extends Button {

    public ImageButton(Images image) {
        super("", new ImageView(image.getImage()));
        getStyleClass().setAll("default-button");

        MainScene.themeProperty().addListener((ov, o, n) -> ((ImageView) getGraphic()).setImage(image.getImage()));
    }

}
