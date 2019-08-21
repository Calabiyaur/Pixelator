package com.calabi.pixelator.view.colorselection;

import javafx.scene.Group;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import com.calabi.pixelator.control.basic.UndeselectableToggleGroup;

public class ColorTabButtons extends Group {

    private final ToggleButton rgb;
    private final ToggleButton hsb;

    public ColorTabButtons() {
        ToggleGroup tg = new UndeselectableToggleGroup();

        rgb = new ToggleButton(ColorSpace.RGB.name());
        hsb = new ToggleButton(ColorSpace.HSB.name());

        tg.getToggles().addAll(rgb, hsb);

        HBox hBox = new HBox(hsb, rgb);
        hBox.setRotate(270);
        hBox.setSpacing(1);
        getChildren().add(hBox);
    }

    public ToggleButton getRgb() {
        return rgb;
    }

    public ToggleButton getHsb() {
        return hsb;
    }
}
