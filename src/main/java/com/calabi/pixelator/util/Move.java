package com.calabi.pixelator.util;

import com.calabi.pixelator.util.meta.Point;

public final class Move {

    public static Point towards(Point origin, Point destination, int distance) {
        int dx = destination.getX() - origin.getX();
        int dy = destination.getY() - origin.getY();
        if (dx == 0 && dy == 0) {
            return origin;
        }
        double actualDistance = NumberUtil.distance(dx, dy);

        int x = (int) Math.round(origin.getX() + dx * (distance / actualDistance));
        int y = (int) Math.round(origin.getY() + dy * (distance / actualDistance));
        return new Point(x, y);
    }

}
