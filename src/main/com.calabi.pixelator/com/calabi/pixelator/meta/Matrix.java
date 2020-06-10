package com.calabi.pixelator.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Matrix<T, P extends Point> {

    protected final List<List<T>> lines;
    protected int size;

    protected final List<P> points;
    protected boolean modified;

    public Matrix() {
        this.lines = new ArrayList<>();
        this.size = 0;
        this.points = new ArrayList<>();
        this.modified = false;
    }

    public final void add(int x, int y, T value) {
        if (x < 0 || y < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (value == null) {
            throw new NullPointerException();
        }

        List<T> line = getOrMakeLine(y);

        addToLine(line, x, value);
    }

    public void add(Matrix<T, P> other) {
        for (int y = 0; y < other.height(); y++) {
            if (other.hasLine(y)) {
                List<T> otherLine = other.getLine(y);
                List<T> line = getOrMakeLine(y);
                for (int x = 0; x < otherLine.size(); x++) {
                    T newValue = otherLine.get(x);
                    if (newValue != null) {
                        addToLine(line, x, newValue);
                    }
                }
            }
        }
    }

    public boolean contains(int x, int y) {
        if (!hasLine(y)) {
            return false;
        }
        List<T> line = getLine(y);
        return line.size() > x && line.get(x) != null;
    }

    public List<P> getPoints() {
        if (modified) {
            points.clear();
            toPoints();
            modified = false;
        }
        return points;
    }

    public void reset() {
        lines.clear();
        size = 0;
        points.clear();
        modified = false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return height() == 0;
    }

    protected abstract void toPoints();

    protected int height() {
        return lines.size();
    }

    protected boolean hasLine(int y) {
        return getLine(y) != null;
    }

    protected List<T> getLine(int y) {
        return height() > y ? lines.get(y) : null;
    }

    protected T getValue(int x, int y) {
        List<T> line = getLine(y);
        return line != null && line.size() > x ? line.get(x) : null;
    }

    protected List<T> getOrMakeLine(int y) {
        while (y > lines.size()) {
            lines.add(null);
        }

        if (y == lines.size()) {
            ArrayList<T> line = new ArrayList<>();
            lines.add(line);
            modified = true;
            return line;
        } else {
            List<T> line = getLine(y);
            if (line == null) {
                line = new ArrayList<>();
                lines.set(y, line);
            }
            return line;
        }
    }

    protected void addToLine(List<T> line, int x, T value) {
        if (x >= line.size()) {
            while (x > line.size()) {
                line.add(null);
            }
            line.add(value);
            size++;
            modified = true;
        } else {
            T prev = line.set(x, value);
            if (!Objects.equals(value, prev)) {
                if (prev == null) {
                    size++;
                }
                modified = true;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointArray other = (PointArray) o;
        return lines.equals(other.lines);
    }

    @Override
    public int hashCode() {
        return lines.hashCode();
    }

}
