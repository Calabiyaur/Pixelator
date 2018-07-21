package main.java.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class GridPaneUtil {

    public static List<Double> getWidthOfChildren(GridPane gridPane) {
        Map<Integer, Double> xValues = new HashMap<>();

        for (Node child : gridPane.getChildren()) {
            Integer index = GridPane.getColumnIndex(child);
            Double value = xValues.get(index);
            double newValue = child.localToParent(child.getLayoutBounds()).getMinX();
            xValues.put(index, value == null ? newValue : Math.min(value, newValue));
        }

        List<Double> sortedXValues = xValues.values().stream().sorted(Double::compareTo).collect(Collectors.toList());
        if (sortedXValues.size() <= 1) {
            return Collections.singletonList(gridPane.getWidth());
        }

        List<Double> widthOfChildren = new ArrayList<>();
        for (int i = 0; i < sortedXValues.size() - 1; i++) {
            Double xValue = sortedXValues.get(i);
            Double nextValue = sortedXValues.get(i + 1);
            widthOfChildren.add(nextValue - xValue - gridPane.getHgap());
        }

        widthOfChildren.add(gridPane.getWidth() - sortedXValues.get(sortedXValues.size() - 1));
        return widthOfChildren;
    }

    public static List<Double> getHeightOfChildren(GridPane gridPane) {
        Map<Integer, Double> yValues = new HashMap<>();

        for (Node child : gridPane.getChildren()) {
            Integer index = GridPane.getRowIndex(child);
            Double value = yValues.get(index);
            double newValue = child.localToParent(child.getLayoutBounds()).getMinY();
            yValues.put(index, value == null ? newValue : Math.min(value, newValue));
        }

        List<Double> sortedYValues = yValues.values().stream().sorted(Double::compareTo).collect(Collectors.toList());
        if (sortedYValues.size() <= 1) {
            return Collections.singletonList(gridPane.getHeight());
        }

        List<Double> heightOfChildren = new ArrayList<>();
        for (int i = 0; i < sortedYValues.size() - 1; i++) {
            Double yValue = sortedYValues.get(i);
            Double nextValue = sortedYValues.get(i + 1);
            heightOfChildren.add(nextValue - yValue - gridPane.getVgap());
        }

        heightOfChildren.add(gridPane.getHeight() - sortedYValues.get(sortedYValues.size() - 1));
        return heightOfChildren;
    }

}
