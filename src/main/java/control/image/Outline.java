package main.java.control.image;

import main.java.meta.PointArray;

public class Outline extends ShapeStack {

    private PointArray points;

    public Outline(int pixelWidth, int pixelHeight) {
        super(pixelWidth, pixelHeight);
    }

    public void draw() {
        if (points == null) {
            return;
        }

    }

    public void setPoints(PointArray points) {
        this.points = points;
        draw();
    }
}