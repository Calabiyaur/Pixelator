package com.calabi.pixelator.control.image;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Grid extends ShapeStack {

    private int xInterval = 1;
    private int yInterval = 1;

    public Grid(int pixelWidth, int pixelHeight) {
        super(pixelWidth, pixelHeight);
    }

    public void draw() {
        getChildren().clear();
        for (int i = xInterval; i < getPixelWidth(); i += xInterval) {
            Line line = new Line();
            line.setStroke(Color.color(0, 0, 0, 0.5));

            line.startXProperty().bind(x(i));
            line.setStartY(0);
            line.endXProperty().bind(x(i));
            line.endYProperty().bind(prefHeightProperty().subtract(1));
            line.translateXProperty().bind(line.startXProperty());

            line.visibleProperty().bind(prefWidthProperty().greaterThan(getPixelWidth()));
            getChildren().add(line);
        }
        for (int j = yInterval; j < getPixelHeight(); j += yInterval) {
            Line line = new Line();
            line.setStroke(Color.color(0, 0, 0, 0.5));

            line.setStartX(0);
            line.startYProperty().bind(y(j));
            line.endXProperty().bind(prefWidthProperty().subtract(1));
            line.endYProperty().bind(y(j));
            line.translateYProperty().bind(line.startYProperty());

            line.visibleProperty().bind(prefHeightProperty().greaterThan(getPixelHeight()));
            getChildren().add(line);
        }
    }

    public void setXInterval(int xInterval) {
        this.xInterval = xInterval;
    }

    public void setYInterval(int yInterval) {
        this.yInterval = yInterval;
    }

}
