package main.java.standard.control.basic;

import javafx.scene.input.MouseEvent;

import main.java.standard.Direction;
import main.java.standard.control.DraggablePane;

public class BasicScrollBar extends DraggablePane {

    public BasicScrollBar(boolean horizontal) {
        setResizable(false);

        if (horizontal) {
            setDragVertical(false);
            setPrefHeight(BasicScrollPane.SCROLL_BAR_HEIGHT);
        } else {
            setDragHorizontal(false);
            setPrefWidth(BasicScrollPane.SCROLL_BAR_WIDTH);
        }
        getStyleClass().setAll("scroll-bar" + (horizontal ? "-horizontal" : "-vertical"));
    }

    @Override public boolean isDraggableHere(MouseEvent event) {
        return true;
    }

    @Override public Direction isResizableHere(MouseEvent event) {
        return null;
    }
}
