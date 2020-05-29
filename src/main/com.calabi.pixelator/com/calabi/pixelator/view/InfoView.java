package com.calabi.pixelator.view;

import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import com.calabi.pixelator.meta.Point;

public class InfoView extends GridPane {

    private static InfoView instance;
    private Label mousePosition;
    private Label colorCount;

    private InfoView() {
        setHgap(6);
        setPadding(new Insets(2));
        setAlignment(Pos.TOP_LEFT);

        mousePosition = new Label();
        colorCount = new Label();
        addRow(0, mousePosition, colorCount);
        GridPane.setHgrow(mousePosition, Priority.ALWAYS);
    }

    public static InfoView get() {
        if (instance == null) {
            instance = new InfoView();
        }
        return instance;
    }

    public static void setMousePosition(Point position) {
        get().mousePosition.setText(position == null ? null : "Mouse: " + position.toString());
    }

    public static BooleanBinding mousePositionVisibleProperty() {
        return get().mousePosition.textProperty().isNotNull();
    }

    public static void setColorCount(Integer count) {
        get().colorCount.setText(count == null ? null : count + " color" + (count > 1 ? "s" : ""));
    }

}
