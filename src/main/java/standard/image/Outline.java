package main.java.standard.image;

import main.java.standard.PointArray;

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
