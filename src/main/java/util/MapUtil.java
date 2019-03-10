package main.java.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static <T, U> Map<T, U> asMap(T[] keys, U[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("Key length must equal value length!");
        }
        int length = keys.length;
        Map<T, U> map = new HashMap<>();
        for (int i = 0; i < length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }
}
