package com.calabi.pixelator.view.palette;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;

public final class SortMaster {

    public static List<Color> sortByValues(Set<Color> colorSet) {
        return colorSet.stream().sorted((o1, o2) -> {
            if (o1 == null || o2 == null) {
                return 0;
            }
            double sum1 = o1.getRed() + o1.getGreen() + o1.getBlue();
            double sum2 = o2.getRed() + o2.getGreen() + o2.getBlue();
            if (sum1 != sum2) {
                return Double.compare(sum1, sum2);
            }
            if (o1.getRed() != o2.getRed()) {
                return Double.compare(o1.getRed(), o2.getRed());
            }
            if (o1.getGreen() != o2.getGreen()) {
                return Double.compare(o1.getGreen(), o2.getGreen());
            }
            return Double.compare(o1.getBlue(), o2.getBlue());
        }).collect(Collectors.toList());
    }

}
