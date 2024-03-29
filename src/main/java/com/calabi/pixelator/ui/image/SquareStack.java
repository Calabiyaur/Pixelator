package com.calabi.pixelator.ui.image;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.util.CollectionUtil;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.meta.PixelArray;
import com.calabi.pixelator.util.meta.Point;
import com.calabi.pixelator.util.meta.PointArray;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.ToolView;

public class SquareStack extends ShapeStack {

    private final PixelArray pixels;

    public SquareStack(int pixelWidth, int pixelHeight) {
        super(pixelWidth, pixelHeight);
        pixels = new PixelArray();
    }

    public void draw() {
    }

    public void define(PointArray points) {
        clear();

        // Maps a row index y to the x values of that row
        Map<Integer, Set<Integer>> rowMap = new HashMap<>();
        points.forEach((x, y) -> {
            Set<Integer> row = rowMap.computeIfAbsent(y, key -> new HashSet<>());
            row.add(x);
        });

        List<Integer> rowIndices = rowMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        Map<Point, Rectangle> squaresOfThePreviousRow = new HashMap<>();
        List<Rectangle> squares = new ArrayList<>();
        for (Integer y : rowIndices) {
            Set<Point> retainedSquares = new HashSet<>();
            Set<Integer> xValues = rowMap.get(y);
            for (Point pair : CollectionUtil.getExtrema(xValues)) {

                Rectangle square = squaresOfThePreviousRow.get(pair);
                if (square == null) {
                    square = new Rectangle(pair.getLeft(), y, pair.getRight() - pair.getLeft() + 1, 1);
                    squaresOfThePreviousRow.put(pair, square);
                    squares.add(square);
                } else {
                    square.setHeight(square.getHeight() + 1);
                }
                retainedSquares.add(pair);

                for (Integer x : xValues) {
                    pixels.add(x, y, null, ColorView.getColor());
                }
            }

            squaresOfThePreviousRow.keySet().retainAll(retainedSquares);
        }

        List<Rectangle> scalableSquares = new ArrayList<>();
        Color transparent = Color.valueOf(Config.IMAGE_BACKGROUND_COLOR.getString());
        for (Rectangle square : squares) {
            Rectangle scalableSquare = scalableSquare(square);
            boolean replaceColor = ToolView.get().isReplaceColor();
            Color color = ColorUtil.addColors(transparent, ColorView.getColor(), !replaceColor, false);
            scalableSquare.setStrokeWidth(0);
            scalableSquare.setFill(color);

            scalableSquares.add(scalableSquare);
        }

        getChildren().addAll(scalableSquares);
    }

    public void clear() {
        getChildren().clear();
        getPixels().reset();
    }

    public PixelArray getPixels() {
        return pixels;
    }
}
