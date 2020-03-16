package com.calabi.pixelator.control.basic;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.calabi.pixelator.res.Images;

public class ToggleImageButton extends ToggleButton {

    public ToggleImageButton(Images image) {
        setGraphic(new ImageView(image.getImage()));
    }

    public ToggleImageButton(ToggleGroup tg, Images image) {
        this(image);
        setToggleGroup(tg);
    }

    public ToggleImageButton(Images image, Images selected) {
        Image image1 = image.getImage();
        Image image2 = selected.getImage();
        ImageView imageView = new ImageView(image1);
        selectedProperty().addListener((ov, o, n) -> {
            if (n) {
                imageView.setImage(image2);
            } else {
                imageView.setImage(image1);
            }
        });

        setGraphic(imageView);
    }

}
