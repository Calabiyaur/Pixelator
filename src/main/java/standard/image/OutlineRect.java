package main.java.standard.image;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.Line;

public class OutlineRect extends ShapeStack {

    private IntegerProperty x1 = new SimpleIntegerProperty();
    private IntegerProperty y1 = new SimpleIntegerProperty();
    private IntegerProperty x2 = new SimpleIntegerProperty();
    private IntegerProperty y2 = new SimpleIntegerProperty();

    public OutlineRect(int pixelWidth, int pixelHeight) {
        super(pixelWidth, pixelHeight);
    }

    public void draw() {
        Line top = scalableLine(x1, y1, x2, y1);
        Line right = scalableLine(x2, y1, x2, y2);
        Line bottom = scalableLine(x1, y2, x2, y2);
        Line left = scalableLine(x1, y1, x1, y2);
        getChildren().setAll(top, right, bottom, left);
    }

    public void move(int dX, int dY) {
        this.x1.set(x1.get() + dX);
        this.y1.set(y1.get() + dY);
        this.x2.set(x2.get() + dX);
        this.y2.set(y2.get() + dY);
    }

    public void setEdges(int x1, int y1, int x2, int y2) {
        this.x1.set(Math.min(getPixelWidth(), Math.max(0, x1 + (x1 > x2 ? 1 : 0))));
        this.y1.set(Math.min(getPixelHeight(), Math.max(0, y1 + (y1 > y2 ? 1 : 0))));
        this.x2.set(Math.min(getPixelWidth(), Math.max(0, x2 + (x2 >= x1 ? 1 : 0))));
        this.y2.set(Math.min(getPixelHeight(), Math.max(0, y2 + (y2 >= y1 ? 1 : 0))));
    }
}
