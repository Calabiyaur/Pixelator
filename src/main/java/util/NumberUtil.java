package main.java.util;

public class NumberUtil {

    public static double round(double number, int afterComma) {
        return Math.round(number * Math.pow(10, afterComma)) / Math.pow(10, afterComma);
    }

    public static double minMax(double min, double value, double max) {
        return Math.min(Math.max(min, value), max);
    }

}
