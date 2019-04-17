package main.java.util;

import java.util.Objects;

public class Check {

    public static void ensure(boolean value) {
        if (!value) {
            throw new IllegalArgumentException();
        }
    }

    public static void ensureEquals(Object o1, Object o2) {
        if (!Objects.equals(o1, o2)) {
            throw new IllegalArgumentException("'" + o1 + "' does not equal '" + o2 + "'!");
        }
    }

    public static void notNull(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
    }

}
