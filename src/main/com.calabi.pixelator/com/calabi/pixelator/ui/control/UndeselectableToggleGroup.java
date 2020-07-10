package com.calabi.pixelator.ui.control;

import javafx.scene.control.ToggleGroup;

public class UndeselectableToggleGroup extends ToggleGroup {

    public UndeselectableToggleGroup() {
        selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null && o != null) {
                o.setSelected(true);
            }
        });
    }
}
