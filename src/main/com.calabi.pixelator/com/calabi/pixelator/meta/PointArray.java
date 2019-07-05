package com.calabi.pixelator.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;

public class PointArray { //TODO: Make 'Array' interface and let this and 'PixelArray' extend it

    ArrayList<Integer> x;
    ArrayList<Integer> y;

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

    public boolean containsAll(PointArray points) {
        for (int i = 0; i < points.size(); i++) {
            if (!contains(points.getX(i), points.getY(i))) {
                return false;
            }
        }
        return true;
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
