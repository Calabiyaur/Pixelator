package com.calabi.pixelator.util;

public final class NumberUtil {

    public static double round(double number, int afterComma) {
        return Math.round(number * Math.pow(10, afterComma)) / Math.pow(10, afterComma);
    }

    public static double minMax(double min, double value, double max) {
        return Math.min(Math.max(min, value), max);
    }

    public static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    public static double distance(double x, double y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public static int sign(int number) {
        return Integer.compare(number, 0);
    }

}
