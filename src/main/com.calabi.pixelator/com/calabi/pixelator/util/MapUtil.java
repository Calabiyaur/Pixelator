package com.calabi.pixelator.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public static <T, U> Set<U> getAll(Map<T, U> map, Collection<? extends T> keys) {
        Set<U> result = new HashSet<>();
        for (T key : keys) {
            result.add(map.get(key));
        }
        result.remove(null);
        return result;
    }
}
