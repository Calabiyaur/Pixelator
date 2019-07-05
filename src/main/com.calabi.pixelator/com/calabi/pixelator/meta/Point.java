package com.calabi.pixelator.meta;

public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Number x, Number y) {
        this.x = x.intValue();
        this.y = y.intValue();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLeft() {
        return getX();
    }

    public int getRight() {
        return getY();
    }

    public Point copy() {
        return new Point(x, y);
    }

    public int distanceMax(Point other) {
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }

    @Override public String toString() {
        return "(" + x + " | " + y + ")";
    }

    @Override public int hashCode() {
        int result = 31;
        result = result + 31 * x;
        result = result + 31 * y;
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point p = (Point) obj;
        return x == p.x && y == p.y;
    }
}
