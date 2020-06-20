package com.calabi.pixelator.util.shape;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.util.NumberUtil;

public final class RectangleHelper {

    /**
     * Return all points that lie between (x1, y1) and (x2, y2),
     * in a rectangular shape.
     */
    public static PointArray getRectanglePoints(Point p1, Point p2, boolean fill, int maxX, int maxY) {
        return getRectanglePoints(p1.getX(), p1.getY(), p2.getX(), p2.getY(), fill, maxX, maxY);
    }

    /**
     * Return all points that lie between (x1, y1) and (x2, y2),
     * in a rectangular shape.
     */
    public static PointArray getRectanglePoints(int x1, int y1, int x2, int y2, boolean fill, int maxX, int maxY) {
        PointArray points = new PointArray();
        int p1x = Math.min(x1, x2);
        int p1y = Math.min(y1, y2);
        int p2x = Math.max(x1, x2);
        int p2y = Math.max(y1, y2);

        for (int x = p1x; x <= p2x; x++) {
            for (int y = p1y; y <= p2y; y++) {
                if (fill || x == p1x || x == p2x || y == p1y || y == p2y) {
                    addPoint(points, x, y, maxX, maxY);
                }
            }
        }
        return points;
    }

    /**
     * Return all points that have a distance <= radius to (x, y).
     * (Using the 1-norm)
     */
    public static PointArray getDiamondPoints(int x, int y, int radius, int maxX, int maxY) {
        PointArray points = new PointArray();
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if (Math.abs(i - x) + Math.abs(j - y) <= radius) {
                    addPoint(points, i, j, maxX, maxY);
                }
            }
        }
        return points;
    }

    /**
     * Return all points that have a distance <= width / 2 to (x, y).
     * (Using the 2-norm)
     */
    public static PointArray getCirclePoints(int x, int y, int width, int maxX, int maxY) {
        PointArray points = new PointArray();
        if (width == 1) {
            addPoint(points, x, y, maxX, maxY);
            return points;
        }
        int radius = width / 2;
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if (NumberUtil.distance(i - x, j - y) <= radius) {
                    addPoint(points, i, j, maxX, maxY);
                }
            }
        }
        return points;
    }

    private static void addPoint(PointArray points, int x, int y, int maxX, int maxY) {
        if (0 <= x && x < maxX && 0 <= y && y < maxY) {
            points.add(x, y);
        }
    }

}
