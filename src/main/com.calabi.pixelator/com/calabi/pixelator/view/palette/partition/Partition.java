package com.calabi.pixelator.view.palette.partition;

import javafx.scene.paint.Color;

public interface Partition {

    void add(Color color);

    Mapping createMapping();
}
