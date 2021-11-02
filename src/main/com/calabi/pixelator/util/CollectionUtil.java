package com.calabi.pixelator.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.calabi.pixelator.meta.Point;

public class CollectionUtil {

    public static <T> Set<T> toSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    public static <T> List<T> toList(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    public static List<Point> getExtrema(Collection<Integer> collection) {
        List<Point> result = new ArrayList<>();
        if (collection.isEmpty()) {
            return result;
        }
        List<Integer> sortedList =
                collection.stream().distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

        Integer previousValue = null;
        Integer lastMinimum = null;
        for (int i : sortedList) {
            if (previousValue == null) {
                lastMinimum = i;
                previousValue = i;
                continue;
            }
            if (i - previousValue > 1) {
                result.add(new Point(lastMinimum, previousValue));
                lastMinimum = i;
            }
            previousValue = i;
        }
        if (previousValue != null) {
            result.add(new Point(lastMinimum, previousValue));
        }

        return result;
    }

    public static List<Integer> intsBetween(int start, int end) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            result.add(i);
        }
        return result;
    }

    public static <T> Set<T> subset(Collection<T> collection, int size) {
        HashSet<T> result = new HashSet<>();
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; iterator.hasNext() && i < size; i++) {
            result.add(iterator.next());
        }
        return result;
    }

    public static <T> Collection<T> reduceEvenly(Collection<T> collection, int reducedSize) {
        List<T> list = new ArrayList<>(collection);
        int n = list.size() - 1;
        int d = reducedSize - 1;
        int r = 0;
        for (Iterator<T> iter = list.iterator(); iter.hasNext();) {
            iter.next();
            if (n == 0) {
                break;
            } else if (r == 0) {
                r = (int) Math.round((double) n / (double) d);
                d--;
            } else {
                iter.remove();
            }
            n--;
            r--;
        }
        return list;
    }

}
