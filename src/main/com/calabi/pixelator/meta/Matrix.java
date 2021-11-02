package com.calabi.pixelator.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class Matrix<T, P extends Point> {

    protected final List<MatrixList<T>> lines;
    protected int size;

    protected final List<P> points;
    protected boolean modified;

    public Matrix() {
        this.lines = new ArrayList<>();
        this.size = 0;
        this.points = new MatrixList<>();
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
            List<T> otherLine = other.getLine(y);
            if (otherLine != null) {
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
        List<T> line = getLine(y);
        if (line == null) {
            return false;
        }
        return line.size() > x && line.get(x) != null;
    }

    public boolean remove(int x, int y) {
        MatrixList<T> line = getLine(y);
        if (line == null) {
            return false;
        }
        boolean success = line.size() > x && line.set(x, null) != null;
        if (success) {
            size--;
            if (line.nonNullEmpty()) {
                lines.remove(line);
            }
        }
        return success;
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
        return size == 0;
    }

    protected abstract void toPoints();

    protected int height() {
        return lines.size();
    }

    protected boolean hasLine(int y) {
        return getLine(y) != null;
    }

    protected MatrixList<T> getLine(int y) {
        return height() > y ? lines.get(y) : null;
    }

    public T getValue(int x, int y) {
        List<T> line = getLine(y);
        return line != null && line.size() > x ? line.get(x) : null;
    }

    protected List<T> getOrMakeLine(int y) {
        while (y > lines.size()) {
            lines.add(null);
        }

        MatrixList<T> line;
        if (y == lines.size()) {
            line = new MatrixList<>();
            lines.add(line);
            modified = true;
        } else {
            line = getLine(y);
            if (line == null) {
                line = new MatrixList<>();
                lines.set(y, line);
            }
        }
        return line;
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

    /**
     * ArrayList extension for counting non-null values
     * //TODO: Not all potentially relevant methods are overwritten.
     */
    protected static class MatrixList<U> extends ArrayList<U> {

        private int nonNullSize = 0;

        public MatrixList() {
            // Default constructor
        }

        public MatrixList(Collection<? extends U> c) {
            super(c);
            if (c instanceof MatrixList) {
                nonNullSize += ((MatrixList<?>) c).nonNullSize;
            } else {
                nonNullSize += c.stream().filter(Objects::nonNull).count();
            }
        }

        public int nonNullSize() {
            return nonNullSize;
        }

        public boolean nonNullEmpty() {
            return nonNullSize == 0;
        }

        @Override
        public boolean add(U u) {
            if (u != null) {
                nonNullSize++;
            }
            return super.add(u);
        }

        @Override
        public void add(int index, U element) {
            if (element != null) {
                nonNullSize++;
            }
            super.add(index, element);
        }

        @Override public boolean addAll(Collection<? extends U> c) {
            if (c instanceof MatrixList) {
                nonNullSize += ((MatrixList<?>) c).nonNullSize;
            } else {
                nonNullSize += c.stream().filter(Objects::nonNull).count();
            }
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends U> c) {
            if (c instanceof MatrixList) {
                nonNullSize += ((MatrixList<?>) c).nonNullSize;
            } else {
                nonNullSize += c.stream().filter(Objects::nonNull).count();
            }
            return super.addAll(index, c);
        }

        @Override
        public U set(int index, U element) {
            U oldValue = super.set(index, element);
            if (oldValue == null) {
                nonNullSize++;
            }
            if (element == null) {
                nonNullSize--;
            }
            return oldValue;
        }

        @Override
        public U remove(int index) {
            U removed = super.remove(index);
            if (removed != null) {
                nonNullSize--;
            }
            return removed;
        }

        @Override
        public boolean remove(Object o) {
            boolean success = super.remove(o);
            if (success && o != null) {
                nonNullSize--;
            }
            return success;
        }

    }

}
