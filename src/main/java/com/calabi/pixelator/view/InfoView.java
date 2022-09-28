package com.calabi.pixelator.view;

import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import com.calabi.pixelator.util.meta.Point;

public class InfoView extends GridPane {

    private static final InfoView instance = new InfoView();
    private static Label mousePosition;
    private static Label selectionSize;
    private static Label colorCount;

    private InfoView() {
        setHgap(6);
        setPadding(new Insets(2));
        setStyle("-fx-background-color: -px_empty_area");
        setAlignment(Pos.TOP_LEFT);

        mousePosition = new Label();
        selectionSize = new Label();
        colorCount = new Label();
        addRow(0, mousePosition, selectionSize, colorCount);
        GridPane.setHgrow(selectionSize, Priority.ALWAYS);
    }

    public static InfoView get() {
        return instance;
    }

    public static void setMousePosition(Point position) {
        mousePosition.setText(position == null ? null : "Mouse: " + position.toString() + "\t");
    }

    public static void setSelectionSize(Point origin, Point end) {
        selectionSize.setText(origin == null || end == null ? null : "Selection: "
                + (Math.abs(end.getX() - origin.getX()) + 1) + " x " + (Math.abs(end.getY() - origin.getY()) + 1));
    }

    public static BooleanBinding mousePositionVisibleProperty() {
        return mousePosition.textProperty().isNotNull();
    }

    public static void setColorCount(Integer count) {
        colorCount.setText(count == null ? null : count + " color" + (count > 1 ? "s" : ""));
    }

}
