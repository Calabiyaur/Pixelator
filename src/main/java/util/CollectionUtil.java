package main.java.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import main.java.meta.Point;

public class CollectionUtil {

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

}
