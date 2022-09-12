package com.calabi.pixelator.ui.control;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.start.MainScene;

public class ToggleImageButton extends ToggleButton {

    public ToggleImageButton(Images image) {
        this(image, image);
    }

    public ToggleImageButton(ToggleGroup tg, Images image) {
        this(image);
        setToggleGroup(tg);
    }

    public ToggleImageButton(Images off, Images on) {
        ImageView imageView = new ImageView(off.getImage());

        selectedProperty().addListener((ov, o, n) -> {
            if (n) {
                imageView.setImage(on.getImage());
            } else {
                imageView.setImage(off.getImage());
            }
        });

        MainScene.themeProperty().addListener((ov, o, n) -> {
            Image image = isSelected() ? on.getImage() : off.getImage();
            ((ImageView) getGraphic()).setImage(image);
        });

        setGraphic(imageView);
    }

}
