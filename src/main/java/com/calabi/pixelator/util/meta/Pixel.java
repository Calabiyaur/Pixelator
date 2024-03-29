package com.calabi.pixelator.util.meta;

import javafx.scene.paint.Color;

public class Pixel extends Point {

    private final Color previousColor;
    private final Color color;

    public Pixel(int x, int y, Color previousColor, Color color) {
        super(x, y);
        this.previousColor = previousColor;
        this.color = color;
    }

    public Color getPreviousColor() {
        return previousColor;
    }

    public Color getColor() {
        return color;
    }

}
