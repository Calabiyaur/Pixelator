package com.calabi.pixelator.ui.image;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.util.CollectionUtil;

public class OutlineShape extends ShapeStack {

    private Timeline timeline = new Timeline();

    public OutlineShape(int pixelWidth, int pixelHeight) {
        super(pixelWidth, pixelHeight);
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void draw() {
        //TODO: Refactor this. Is the existence of this method justified?
    }

    public void define(PointArray points) {
        getChildren().clear();

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Maps a row index y to the x values of that row
        Map<Integer, List<Integer>> rowMap = new HashMap<>();
        points.forEach((x, y) -> {
            List<Integer> row = rowMap.computeIfAbsent(y, key -> new ArrayList<>());
            row.add(x);
        });

        // Gather the vertical lines on the edge of each continuous series of points in a line
        List<Integer> rowIndices = rowMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        Map<Integer, Line> linesOfThePreviousRowLeft = new HashMap<>();
        Map<Integer, Line> linesOfThePreviousRowRight = new HashMap<>();
        List<Line> leftLines = new ArrayList<>();
        List<Line> rightLines = new ArrayList<>();
        int lastRowIndex = -1;
        for (int y : rowIndices) {
            if (y - lastRowIndex > 1) {
                linesOfThePreviousRowLeft.clear(); //TODO: Potential time-safe by not filling them here in the first place
                linesOfThePreviousRowRight.clear();
            }
            lastRowIndex = y;
            Set<Integer> retainLeft = new HashSet<>();
            Set<Integer> retainRight = new HashSet<>();
            for (Point pair : CollectionUtil.getExtrema(rowMap.get(y))) {

                int leftLineX = pair.getLeft();
                Line leftLine = linesOfThePreviousRowLeft.get(leftLineX);
                if (leftLine == null) {
                    leftLine = new Line(leftLineX, y, leftLineX, y + 1);
                    linesOfThePreviousRowLeft.put(leftLineX, leftLine);
                    leftLines.add(leftLine);
                } else {
                    leftLine.setEndY(y + 1);
                }
                retainLeft.add(leftLineX);

                int rightLineX = pair.getRight();
                Line rightLine = linesOfThePreviousRowRight.get(rightLineX);
                if (rightLine == null) {
                    rightLine = new Line(rightLineX + 1, y, rightLineX + 1, y + 1);
                    linesOfThePreviousRowRight.put(rightLineX, rightLine);
                    rightLines.add(rightLine);
                } else {
                    rightLine.setEndY(y + 1);
                }
                retainRight.add(rightLineX);
            }

            linesOfThePreviousRowLeft.keySet().retainAll(retainLeft);
            linesOfThePreviousRowRight.keySet().retainAll(retainRight);
        }

        // Maps a column index x to the y values of that column
        Map<Integer, List<Integer>> columnMap = new HashMap<>();
        points.forEach((x, y) -> {
            List<Integer> column = columnMap.computeIfAbsent(x, key -> new ArrayList<>());
            column.add(y);
        });

        // Gather the horizontal lines on the edge of each continuous series of points in a line
        List<Integer> columnIndices = columnMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        Map<Integer, Line> linesOfThePreviousColumnTop = new HashMap<>();
        Map<Integer, Line> linesOfThePreviousColumnBottom = new HashMap<>();
        List<Line> topLines = new ArrayList<>();
        List<Line> bottomLines = new ArrayList<>();
        int lastColumnIndex = -1;
        for (int x : columnIndices) {
            if (x - lastColumnIndex > 1) {
                linesOfThePreviousColumnTop.clear();
                linesOfThePreviousColumnBottom.clear();
            }
            lastColumnIndex = x;
            Set<Integer> retainTop = new HashSet<>();
            Set<Integer> retainBottom = new HashSet<>();
            for (Point pair : CollectionUtil.getExtrema(columnMap.get(x))) {

                int topLineY = pair.getLeft();
                Line topLine = linesOfThePreviousColumnTop.get(topLineY);
                if (topLine == null) {
                    topLine = new Line(x, topLineY, x + 1, topLineY);
                    linesOfThePreviousColumnTop.put(topLineY, topLine);
                    topLines.add(topLine);
                } else {
                    topLine.setEndX(x + 1);
                }
                retainTop.add(topLineY);

                int bottomLineY = pair.getRight();
                Line bottomLine = linesOfThePreviousColumnBottom.get(bottomLineY);
                if (bottomLine == null) {
                    bottomLine = new Line(x, bottomLineY + 1, x + 1, bottomLineY + 1);
                    linesOfThePreviousColumnBottom.put(bottomLineY, bottomLine);
                    bottomLines.add(bottomLine);
                } else {
                    bottomLine.setEndX(x + 1);
                }
                retainBottom.add(bottomLineY);
            }

            linesOfThePreviousColumnTop.keySet().retainAll(retainTop);
            linesOfThePreviousColumnBottom.keySet().retainAll(retainBottom);
        }

        // Create scalable white / dashed black lines from the gathered data
        List<Line> whiteLines = new ArrayList<>();
        List<Line> dashedLines = new ArrayList<>();
        for (Line leftLine : leftLines) {
            whiteLines.add(scalableLine(leftLine, 0, 0, 0, 1));
            dashedLines.add(scalableLine(leftLine, 0, 0, 0, 1));
        }
        for (Line rightLine : rightLines) {
            whiteLines.add(scalableLine(rightLine, -1, 0, 0, 1));
            dashedLines.add(scalableLine(rightLine, -1, 0, 0, 1));
        }
        for (Line topLine : topLines) {
            whiteLines.add(scalableLine(topLine, 0, 0, 1, 0));
            dashedLines.add(scalableLine(topLine, 0, 0, 1, 0));
        }
        for (Line bottomLine : bottomLines) {
            whiteLines.add(scalableLine(bottomLine, 0, -1, 1, 0));
            dashedLines.add(scalableLine(bottomLine, 0, -1, 1, 0));
        }
        whiteLines.forEach(line -> line.setStroke(Color.WHITE));
        dashedLines.forEach(line -> {
            line.getStrokeDashArray().addAll(4d, 4d);
            timeline.getKeyFrames().addAll(
                    new KeyFrame(
                            Duration.ZERO,
                            new KeyValue(
                                    line.strokeDashOffsetProperty(),
                                    0,
                                    Interpolator.LINEAR
                            )
                    ),
                    new KeyFrame(
                            Duration.seconds(1),
                            new KeyValue(
                                    line.strokeDashOffsetProperty(),
                                    8,
                                    Interpolator.LINEAR
                            )
                    )
            );
        });
        getChildren().addAll(whiteLines);
        getChildren().addAll(dashedLines);
        timeline.play();
    }

    public void clear() {
        getChildren().clear();
        timeline.stop();
    }

    public void playAnimation(boolean play) {
        if (play) {
            timeline.play();
        } else {
            timeline.pause();
        }
    }
}
