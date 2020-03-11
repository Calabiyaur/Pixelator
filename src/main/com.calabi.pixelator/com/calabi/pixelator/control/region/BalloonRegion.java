package com.calabi.pixelator.control.region;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class BalloonRegion extends Region {

    public BalloonRegion() {
        HBox.setHgrow(this, Priority.ALWAYS);
    }

}
