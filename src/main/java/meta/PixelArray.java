package main.java.meta;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class PixelArray extends PointArray {

    ArrayList<Color> previousColor;
    ArrayList<Color> color;

    public PixelArray() {
        this.previousColor = new ArrayList<>();
        this.color = new ArrayList<>();
    }

    public void add(int x, int y, Color previousColor, Color color) {
        super.add(x, y);
        this.previousColor.add(previousColor);
        this.color.add(color);
    }

    public void add(PixelArray other) {
        for (int i = 0; i < other.size(); i++) {
            add(other.getX(i), other.getY(i), other.getPreviousColor(i), other.getColor(i));
        }
    }

    public Color getPreviousColor(int index) {
        return previousColor.get(index);
    }

    public Color getColor(int index) {
        return color.get(index);
    }

    public void setColor(int index, Color color) {
        this.color.set(index, color);
    }

    public void forEach(QuadConsumer<Integer, Integer, Color, Color> action) {
        for (int i = 0; i < size(); i++) {
            action.accept(x.get(i), y.get(i), previousColor.get(i), color.get(i));
        }
    }

    @Override
    public PixelArray clone() {
        PixelArray pointArray = new PixelArray();
        pointArray.x = new ArrayList<>(x);
        pointArray.y = new ArrayList<>(y);
        pointArray.previousColor = new ArrayList<>(previousColor);
        pointArray.color = new ArrayList<>(color);
        return pointArray;
    }

    @Override
    public void reset() {
        super.reset();
        color = new ArrayList<>();
        previousColor = new ArrayList<>();
    }

}
