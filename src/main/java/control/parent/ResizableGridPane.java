package main.java.control.parent;

import java.util.Collections;

import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class ResizableGridPane extends GridPane {

    public ResizableGridPane() {
        setOnMousePressed(e -> {
            ResizableGridPane t = this;
            System.out.println();
        });
    }

    public void setPrefWidth(int columnIndex, double prefWidth) {
        createColumnConstraintsUpTo(columnIndex);
        getColumnConstraints().get(columnIndex).setPrefWidth(prefWidth);
    }

    public void setPrefHeight(int rowIndex, double prefHeight) {
        createRowConstraintsUpTo(rowIndex);
        getRowConstraints().get(rowIndex).setPrefHeight(prefHeight);
    }

    public void setHgrow(int columnIndex, Priority priority) {
        createColumnConstraintsUpTo(columnIndex);
        getColumnConstraints().get(columnIndex).setHgrow(priority);
    }

    public void setVgrow(int rowIndex, Priority priority) {
        createRowConstraintsUpTo(rowIndex);
        getRowConstraints().get(rowIndex).setVgrow(priority);
    }

    private void createColumnConstraintsUpTo(int columnIndex) {
        if (getColumnConstraints().size() <= columnIndex) {
            getColumnConstraints().addAll(
                    Collections.nCopies(columnIndex - getColumnConstraints().size() + 1,
                    new ColumnConstraints()));
        }
    }

    private void createRowConstraintsUpTo(int rowIndex) {
        if (getRowConstraints().size() <= rowIndex) {
            getRowConstraints().addAll(
                    Collections.nCopies(rowIndex - getRowConstraints().size() + 1,
                    new RowConstraints()));
        }
    }

    public DoubleProperty prefWidthProperty(int columnIndex) {
        return getColumnConstraints().get(columnIndex).prefWidthProperty();
    }

    public DoubleProperty prefHeightProperty(int rowIndex) {
        return getRowConstraints().get(rowIndex).prefHeightProperty();
    }

}
