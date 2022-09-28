package com.calabi.pixelator.view.palette.partition;

import java.util.Map;

import javafx.scene.paint.Color;

import com.calabi.pixelator.util.meta.Point;

public class Mapping {

    private final int width;
    private final int height;
    private final Map<Color, Point> mapping;

    public Mapping(int width, int height, Map<Color, Point> mapping) {
        this.width = width;
        this.height = height;
        this.mapping = mapping;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Map<Color, Point> get() {
        return mapping;
    }
}
