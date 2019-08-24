package com.calabi.pixelator.util;

import com.calabi.pixelator.meta.Point;

public class Rotate {

    /**
     * Rotate the point "rotate" counter-clockwise around the point "center"
     */
    public static Point rotateLeft(Point center, Point rotate) {
        return rotateLeft(center, rotate.getX(), rotate.getY());
    }

    public static Point rotateLeft(Point center, int rotateX, int rotateY) {
        int x = center.getX() + (rotateY - center.getY());
        int y = center.getY() - (rotateX - center.getX());
        return new Point(x, y);
    }

    /**
     * Rotate the point "rotate" counter-clockwise around the point "center"
     */
    public static Point rotateRight(Point center, Point rotate) {
        return rotateRight(center, rotate.getX(), rotate.getY());
    }

    public static Point rotateRight(Point center, int rotateX, int rotateY) {
        int x = center.getX() - (rotateY - center.getY());
        int y = center.getY() + (rotateX - center.getX());
        return new Point(x, y);
    }

}
