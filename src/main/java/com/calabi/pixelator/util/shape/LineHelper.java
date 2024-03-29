package com.calabi.pixelator.util.shape;

import com.calabi.pixelator.util.NumberUtil;
import com.calabi.pixelator.util.meta.Point;
import com.calabi.pixelator.util.meta.PointArray;

public final class LineHelper {

    /**
     * Return all points which lie between (x1, y1) and (x2, y2),
     * in a straight line. End points included.
     */
    public static PointArray getLinePoints(Point p1, Point p2, int maxX, int maxY) {
        return getLinePoints(p1.getX(), p1.getY(), p2.getX(), p2.getY(), maxX, maxY);
    }

    public static PointArray getLinePoints(int x1, int y1, int x2, int y2, int maxX, int maxY) {
        PointArray points = new PointArray();

        int yDiff = y1 - y2;
        int ySign = yDiff < 0 ? 1 : -1;
        int xDiff = x1 - x2;
        int xSign = xDiff < 0 ? 1 : -1;
        if (yDiff == 0 && xDiff == 0) {
            addPoint(points, x1, y1, maxX, maxY);
            return points;
        }
        double slope = (double) yDiff / (double) xDiff;

        if (Math.abs(slope) <= 1) {
            int x, y;
            for (int i = 0; i <= Math.abs(xDiff); i++) {
                x = x1 + xSign * i;
                y = y1 + (int) Math.round(ySign * i * Math.abs(slope));
                addPoint(points, x, y, maxX, maxY);
            }
        } else {
            int x, y;
            for (int i = 0; i <= Math.abs(yDiff); i++) {
                x = x1 + (int) Math.round(xSign * i / Math.abs(slope));
                y = y1 + ySign * i;
                addPoint(points, x, y, maxX, maxY);
            }
        }

        return points;
    }

    public static PointArray getStraightPath(Point p1, Point p2, int distance) {
        int maxX = Integer.MAX_VALUE;
        int maxY = Integer.MAX_VALUE;

        int dX = p2.getX() - p1.getX();
        int dY = p2.getY() - p1.getY();

        PointArray path = new PointArray();
        int x = p1.getX();
        int y = p1.getY();
        addPoint(path, x, y, maxX, maxY);

        if (Math.abs(dX) >= Math.abs(dY)) {
            x += dX > 0 ? 1 : -1;
        } else {
            y += dY > 0 ? 1 : -1;
        }
        addPoint(path, x, y, maxX, maxY);

        while (NumberUtil.distance(x - p1.getX(), y - p1.getY()) < distance - 1) {
            int totalX = x - p1.getX();
            int totalY = y - p1.getY();
            if (dY == 0 || ((double) totalX / dX) < ((double) totalY / dY)) {
                x += dX > 0 ? 1 : -1;
            } else {
                y += dY > 0 ? 1 : -1;
            }
            addPoint(path, x, y, maxX, maxY);
        }

        return path;
    }

    private static void addPoint(PointArray points, int x, int y, int maxX, int maxY) {
        if (0 <= x && x < maxX && 0 <= y && y < maxY) {
            points.add(x, y);
        }
    }

}
