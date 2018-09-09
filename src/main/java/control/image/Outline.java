package main.java.control.image;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import main.java.meta.Point;
import main.java.meta.PointArray;

public class Outline extends ShapeStack {

    private ObservableList<Point> points;

    public Outline(int pixelWidth, int pixelHeight) {
        super(pixelWidth, pixelHeight);
    }

    public void draw() {
        if (points == null) {
            return;
        }

    }

    public void setPoints(PointArray points) {
        this.points = FXCollections.observableArrayList();


    }
}
