package com.calabi.pixelator.control.basic;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import com.calabi.pixelator.res.Images;

public class ImageButton extends Button {

    public ImageButton(Images image) {
        super("", new ImageView(image.getImage()));
        getStyleClass().setAll("default-button");
    }
}
