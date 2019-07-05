package com.calabi.pixelator.meta;

import javafx.scene.paint.Color;

public class Pixel extends Point {

    private Color previousColor;
    private Color color;

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

    public void setColor(Color color) {
        this.color = color;
    }
}
