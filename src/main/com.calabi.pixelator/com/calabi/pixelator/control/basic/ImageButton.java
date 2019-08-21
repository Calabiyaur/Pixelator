package com.calabi.pixelator.control.basic;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import com.calabi.pixelator.res.Images;

public class ImageButton extends Button {

    public ImageButton(Images image) {
        this(new ImageView(image.getImage()));
    }

    public ImageButton(ImageView imageView) {
        super("", imageView);
        getStyleClass().setAll("default-button");
    }
}
