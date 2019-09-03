package com.calabi.pixelator.util.shape;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;

public class LineHelper {

    /**
     * Return all points which lie between (x1, y1) and (x2, y2),
     * in a straight line. End points included.
     */
    public static PointArray getLinePoints(Point p1, Point p2) {
        return getLinePoints(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static PointArray getLinePoints(int x1, int y1, int x2, int y2) {
        PointArray points = new PointArray();

        int yDiff = y1 - y2;
        int ySign = yDiff < 0 ? 1 : -1;
        int xDiff = x1 - x2;
        int xSign = xDiff < 0 ? 1 : -1;
        if (yDiff == 0 && xDiff == 0) {
            points.add(x1, y1);
            return points;
        }
        double slope = (double) yDiff / (double) xDiff;

        if (Math.abs(slope) <= 1) {
            int x, y;
            for (int i = 0; i <= Math.abs(xDiff); i++) {
                x = x1 + xSign * i;
                y = y1 + (int) Math.round(ySign * i * Math.abs(slope));
                points.add(x, y);
            }
        } else {
            int x, y;
            for (int i = 0; i <= Math.abs(yDiff); i++) {
                x = x1 + (int) Math.round(xSign * i / Math.abs(slope));
                y = y1 + ySign * i;
                points.add(x, y);
            }
        }

        return points;
    }

}