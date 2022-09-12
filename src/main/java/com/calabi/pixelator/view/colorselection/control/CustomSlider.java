package com.calabi.pixelator.view.colorselection.control;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;

import com.calabi.pixelator.ui.control.CustomSliderSkin;

public class CustomSlider extends Slider {

    private DoubleProperty target;

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomSliderSkin(this);
    }

    public DoubleProperty getTarget() {
        return target;
    }

    public void setTarget(DoubleProperty target) {
        this.target = target;
    }

}
