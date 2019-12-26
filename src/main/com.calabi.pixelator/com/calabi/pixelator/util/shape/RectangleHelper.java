package com.calabi.pixelator.util.shape;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;

public class RectangleHelper {

    /**
     * Return all points that lie between (x1, y1) and (x2, y2),
     * in a rectangular shape.
     */
    public static PointArray getRectanglePoints(Point p1, Point p2, boolean fill) {
        PointArray points = new PointArray();
        int x1 = Math.min(p1.getX(), p2.getX());
        int y1 = Math.min(p1.getY(), p2.getY());
        int x2 = Math.max(p1.getX(), p2.getX());
        int y2 = Math.max(p1.getY(), p2.getY());

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (fill || x == x1 || x == x2 || y == y1 || y == y2) {
                    points.add(x, y);
                }
            }
        }
        return points;
    }

    /**
     * Return all points that have a distance <= radius to (x, y).
     * (Using the 1-norm)
     */
    public static PointArray getDiamondPoints(int x, int y, int radius) {
        PointArray points = new PointArray();
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if (Math.abs(i - x) + Math.abs(j - y) <= radius) {
                    points.add(i, j);
                }
            }
        }
        return points;
    }

    /**
     * Return all points that have a distance <= width / 2 to (x, y).
     * (Using the 2-norm)
     */
    public static PointArray getCirclePoints(int x, int y, int width) {
        PointArray points = new PointArray();
        if (width == 1) {
            points.add(x, y);
            return points;
        }
        int radius = width / 2;
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if (Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2)) <= radius) {
                    points.add(i, j);
                }
            }
        }
        return points;
    }

}
