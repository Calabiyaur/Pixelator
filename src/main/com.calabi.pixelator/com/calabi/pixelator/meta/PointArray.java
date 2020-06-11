package com.calabi.pixelator.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;

public class PointArray extends Matrix<Boolean, Point> {

    public final void add(int x, int y) {
        super.add(x, y, Boolean.TRUE);
    }

    public final void add(Matrix<Boolean, Point> other) {
        for (int y = 0; y < other.height(); y++) {
            List<Boolean> otherLine = other.getLine(y);
            if (otherLine != null) {
                List<Boolean> line = getOrMakeLine(y);
                for (int x = 0; x < otherLine.size(); x++) {
                    boolean newValue = Boolean.TRUE.equals(line.get(x)) || Boolean.TRUE.equals(otherLine.get(x));
                    if (newValue) {
                        addToLine(line, x, newValue);
                    }
                }
            }
        }
    }

    public void forEach(BiConsumer<Integer, Integer> action) {
        for (int j = 0; j < height(); j++) {
            List<Boolean> line = getLine(j);
            if (line != null) {
                for (int i = 0; i < line.size(); i++) {
                    Boolean value = line.get(i);
                    if (Boolean.TRUE.equals(value)) {
                        action.accept(i, j);
                    }
                }
            }
        }
    }

    public void subtract(PointArray other) {
        for (int y = 0; y < Math.min(lines.size(), other.height()); y++) {
            List<Boolean> line = getLine(y);
            List<Boolean> otherLine = other.getLine(y);
            if (line != null && otherLine != null) {
                for (int x = 0; x < Math.min(line.size(), otherLine.size()); x++) {
                    if (Boolean.TRUE.equals(line.get(x)) && Boolean.TRUE.equals(otherLine.get(x))) {
                        line.set(x, false);
                        modified = true;
                    }
                }
            }
        }
    }

    public void invert(int maxX, int maxY) {
        for (int y = 0; y < maxY; y++) {
            List<Boolean> line;
            if (y >= lines.size()) {
                line = new ArrayList<>();
                lines.add(line);
            } else {
                line = lines.get(y);
            }
            for (int x = 0; x < maxX; x++) {
                if (x < line.size()) {
                    line.set(x, !line.get(x));
                    modified = true;
                } else {
                    line.add(true);
                    modified = true;
                }
            }
        }
    }

    @Override
    protected void toPoints() {
        forEach((x, y) -> points.add(new Point(x, y)));
    }

    public PointArray copy() {
        PointArray pointArray = new PointArray();
        for (List<Boolean> line : lines) {
            pointArray.lines.add(line == null ? null : new ArrayList<>(line));
        }
        pointArray.modified = true;
        return pointArray;
    }

    public String toString() {
        List<String> s = new ArrayList<>();
        forEach((x, y) -> s.add(String.format("(%s, %s)", x, y)));
        return StringUtils.join(s, "; ");
    }

}
