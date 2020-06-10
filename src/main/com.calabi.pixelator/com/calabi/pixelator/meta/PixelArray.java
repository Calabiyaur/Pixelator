package com.calabi.pixelator.meta;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

import static com.calabi.pixelator.meta.PixelArray.Colors;

public class PixelArray extends Matrix<Colors, Pixel> {

    public void add(int x, int y, Color previousColor, Color color) {
        super.add(x, y, new Colors(previousColor, color));
    }

    public void forEach(QuadConsumer<Integer, Integer, Color, Color> action) {
        for (int y = 0; y < height(); y++) {
            if (hasLine(y)) {
                List<Colors> line = lines.get(y);
                for (int x = 0; x < line.size(); x++) {
                    Colors value = line.get(x);
                    if (value != null) {
                        action.accept(x, y, value.previousColor, value.color);
                    }
                }
            }
        }
    }

    public PointArray toPointArray() {
        PointArray pointArray = new PointArray();
        forEach((x, y, previousColor, color) -> {
            pointArray.add(x, y);
        });
        return pointArray;
    }

    public final Color getColor(int x, int y) {
        Colors colors = getValue(x, y);
        return colors == null ? null : colors.color;
    }

    public final Color getPreviousColor(int x, int y) {
        Colors colors = getValue(x, y);
        return colors == null ? null : colors.previousColor;
    }

    @Override
    protected void toPoints() {
        forEach((x, y, previousColor, color) -> points.add(new Pixel(x, y, previousColor, color)));
    }

    public PixelArray copy() {
        PixelArray pointArray = new PixelArray();
        for (List<Colors> line : lines) {
            pointArray.lines.add(line == null ? null : new ArrayList<>(line));
        }
        pointArray.modified = true;
        return pointArray;
    }

    static class Colors {

        private final Color previousColor;
        private final Color color;

        public Colors(Color previousColor, Color color) {
            this.previousColor = previousColor;
            this.color = color;
        }
    }

}
