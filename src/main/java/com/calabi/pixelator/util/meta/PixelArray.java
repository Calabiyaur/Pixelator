package com.calabi.pixelator.util.meta;

import java.util.List;
import java.util.Objects;

import javafx.scene.paint.Color;

import static com.calabi.pixelator.util.meta.PixelArray.Colors;

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
            pointArray.lines.add(line == null ? null : new MatrixList<>(line));
        }
        pointArray.modified = true;
        return pointArray;
    }

    public static class Colors {

        private Color previousColor;
        private Color color;

        public Colors(Color previousColor, Color color) {
            this.previousColor = previousColor;
            this.color = color;
        }

        public Color getPreviousColor() {
            return previousColor;
        }

        public void setPreviousColor(Color previousColor) {
            this.previousColor = previousColor;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Colors colors = (Colors) o;
            return Objects.equals(previousColor, colors.previousColor) &&
                    Objects.equals(color, colors.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(previousColor, color);
        }

    }

}
