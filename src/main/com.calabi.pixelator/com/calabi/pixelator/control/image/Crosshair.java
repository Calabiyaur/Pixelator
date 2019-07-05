package com.calabi.pixelator.control.image;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.calabi.pixelator.meta.Point;

public class Crosshair extends ShapeStack {

    private IntegerProperty pixelX = new SimpleIntegerProperty(0);
    private IntegerProperty pixelY = new SimpleIntegerProperty(0);

    public Crosshair(int pixelWidth, int pixelHeight) {
        super(pixelWidth, pixelHeight);
    }

    public void draw() {
        getChildren().clear();

        Rectangle right = new Rectangle();
        Rectangle top = new Rectangle();
        Rectangle left = new Rectangle();
        Rectangle bottom = new Rectangle();

        Color color = Color.color(0, 0, 0, 0.5);
        right.setFill(color);
        top.setFill(color);
        left.setFill(color);
        bottom.setFill(color);

        right.xProperty().bind(x(pixelX.add(1)));
        right.yProperty().bind(y(pixelY));
        right.widthProperty().bind(x(pixelWidth.subtract(pixelX).subtract(1)));
        right.heightProperty().bind(y(1));

        top.xProperty().bind(x(pixelX));
        top.widthProperty().bind(x(1));
        top.heightProperty().bind(y(pixelY));

        left.yProperty().bind(y(pixelY));
        left.widthProperty().bind(x(pixelX));
        left.heightProperty().bind(y(1));

        bottom.xProperty().bind(x(pixelX));
        bottom.yProperty().bind(y(pixelY.add(1)));
        bottom.widthProperty().bind(x(1));
        bottom.heightProperty().bind(y(pixelHeight.subtract(pixelY).subtract(1)));

        right.translateXProperty().bind(right.xProperty());
        right.translateYProperty().bind(right.yProperty());
        top.translateXProperty().bind(top.xProperty());
        left.translateYProperty().bind(left.yProperty());
        bottom.translateXProperty().bind(bottom.xProperty());
        bottom.translateYProperty().bind(bottom.yProperty());

        getChildren().addAll(right, top, left, bottom);
    }

    public void setPosition(Point position) {
        pixelX.setValue(position.getX());
        pixelY.setValue(position.getY());
    }

}
