package main.java.util;

public class Check {

    public static void ensure(boolean value) {
        if (!value) {
            throw new IllegalArgumentException();
        }
    }

    public static void notNull(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
    }

}
