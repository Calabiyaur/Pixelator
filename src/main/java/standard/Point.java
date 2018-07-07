package main.java.standard;

public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
        if (obj == null || !(obj instanceof Point)) {
            return false;
        }
        Point p = (Point) obj;
        return x == p.x && y == p.y;
    }
}
