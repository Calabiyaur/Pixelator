package com.calabi.pixelator.control.basic;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
        ImageView imageView = new ImageView(image.getImage());
        ImageView selectedView = new ImageView(selected.getImage());
        selectedProperty().addListener((ov, o, n) -> {
            if (n) {
                setGraphic(selectedView);
            } else {
                setGraphic(imageView);
            }
        });

        setGraphic(imageView);
    }

}
