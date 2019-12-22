package com.calabi.pixelator.meta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;

public class PointArray { //TODO: Make 'Array' interface and let this and 'PixelArray' extend it

    List<Integer> x;
    List<Integer> y;

    public PointArray() {
        this.x = new ArrayList<>();
        this.y = new ArrayList<>();
    }

    public void add(int x, int y) {
        this.x.add(x);
        this.y.add(y);
    }

    public void add(PointArray other) {
        for (int i = 0; i < other.size(); i++) {
            add(other.getX(i), other.getY(i));
        }
    }

    public int getX(int index) {
        return x.get(index);
    }

    public int getY(int index) {
        return y.get(index);
    }

    public int size() {
        return x.size();
    }

    public void forEach(BiConsumer<Integer, Integer> action) {
        for (int i = 0; i < size(); i++) {
            action.accept(x.get(i), y.get(i));
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(int px, int py) {
        for (int i = 0; i < size(); i++) {
            if (x.get(i) == px && y.get(i) == py) {
                return true;
            }
        }
        return false;
    }

    public void addExclusive(PointArray other) {
        other.forEach((px, py) -> addExclusive(px, py));
    }

    public void addExclusive(int px, int py) {
        if (!this.contains(px, py)) {
            this.add(px, py);
        }
    }

    public void subtract(PointArray other) {
        other.forEach((px, py) -> subtract(px, py));
    }

    public void subtract(int px, int py) {
        Iterator<Integer> xIter = x.iterator();
        Iterator<Integer> yIter = y.iterator();
        while (xIter.hasNext()) {
            Integer tx = xIter.next();
            Integer ty = yIter.next();
            if (tx.equals(px) && ty.equals(py)) {
                xIter.remove();
                yIter.remove();
                break;
            }
        }
    }

    public PointArray invert(int maxX, int maxY) {
        List<List<Boolean>> lines = to2d();
        for (int j = 0; j < maxY; j++) {
            List<Boolean> line;
            if (j >= lines.size()) {
                line = new ArrayList<>();
                lines.add(line);
            } else {
                line = lines.get(j);
            }
            for (int i = 0; i < maxX; i++) {
                if (i < line.size()) {
                    line.set(i, !line.get(i));
                } else {
                    line.add(true);
                }
            }
        }
        return from2d(lines);
    }

    private List<List<Boolean>> to2d() {
        List<List<Boolean>> lines = new ArrayList<>();
        forEach((px, py) -> {
            List<Boolean> line;
            if (py >= lines.size()) {
                line = new ArrayList<>();
                lines.add(line);
                while (py >= lines.size()) {
                    line = new ArrayList<>();
                    lines.add(line);
                }
            } else {
                line = lines.get(py);
            }
            if (px >= line.size()) {
                while (px > line.size()) {
                    line.add(false);
                }
                line.add(true);
            } else {
                line.set(px, true);
            }
        });
        return lines;
    }

    private PointArray from2d(List<List<Boolean>> lines) {
        PointArray result = new PointArray();
        for (int j = 0; j < lines.size(); j++) {
            List<Boolean> line = lines.get(j);
            for (int i = 0; i < line.size(); i++) {
                if (line.get(i)) {
                    result.add(i, j);
                }
            }
        }
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointArray other = (PointArray) o;
        return x.equals(other.x) && y.equals(other.y);
    }

    @Override public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }

    public PointArray clone() {
        PointArray pointArray = new PointArray();
        pointArray.x = new ArrayList<>(x);
        pointArray.y = new ArrayList<>(y);
        return pointArray;
    }

    public String toString() {
        List<String> s = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            s.add(String.format("(%s, %s)", x.get(i), y.get(i)));
        }
        return StringUtils.join(s, "; ");
    }

    public void reset() {
        x = new ArrayList<>();
        y = new ArrayList<>();
    }

}
