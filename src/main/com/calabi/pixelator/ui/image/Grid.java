package com.calabi.pixelator.ui.image;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class Grid extends ShapeStack {

    private int xInterval = 1;
    private int yInterval = 1;
    private Color color;

    public Grid(int pixelWidth, int pixelHeight, Color color) {
        super(pixelWidth, pixelHeight);
        this.color = color;
    }

    public void draw() {
        getChildren().clear();
        for (int i = xInterval; i < getPixelWidth(); i += xInterval) {
            Line line = new Line();
            line.setStroke(color);

            line.startXProperty().bind(x(i));
            line.setStartY(0);
            line.endXProperty().bind(x(i));
            line.endYProperty().bind(prefHeightProperty().subtract(1));
            line.translateXProperty().bind(line.startXProperty());

            line.visibleProperty().bind(prefWidthProperty().multiply(Math.min(2, xInterval)).greaterThan(getPixelWidth()));
            getChildren().add(line);
        }
        for (int j = yInterval; j < getPixelHeight(); j += yInterval) {
            Line line = new Line();
            line.setStroke(color);

            line.setStartX(0);
            line.startYProperty().bind(y(j));
            line.endXProperty().bind(prefWidthProperty().subtract(1));
            line.endYProperty().bind(y(j));
            line.translateYProperty().bind(line.startYProperty());

            line.visibleProperty().bind(prefHeightProperty().multiply(Math.min(2, yInterval)).greaterThan(getPixelHeight()));
            getChildren().add(line);
        }
    }

    public void setXInterval(int xInterval) {
        this.xInterval = xInterval;
    }

    public void setYInterval(int yInterval) {
        this.yInterval = yInterval;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        for (Node child : getChildren()) {
            ((Shape) child).setStroke(color);
        }
    }

}
