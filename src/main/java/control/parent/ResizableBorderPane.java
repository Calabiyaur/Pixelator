package main.java.control.parent;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class ResizableBorderPane extends BorderPane {

    private final int RESIZE_MARGIN = 6;
    private double previousX = 0;
    private double previousY = 0;
    private Region dragging;
    private Position dragPos = null;

    public ResizableBorderPane() {
        setOnMousePressed(event -> mousePressed(event));
        setOnMouseDragged(event -> mouseDragged(event));
        setOnMouseMoved(event -> mouseOver(event));
        setOnMouseReleased(event -> mouseReleased(event));
    }

    protected void mousePressed(MouseEvent event) {
        dragging = getComponent(event, true);

        // ignore clicks outside of the draggable margin
        if (dragging == null) {
            return;
        }

        previousX = event.getX();
        previousY = event.getY();

    }

    protected void mouseDragged(MouseEvent event) {
        if (dragging == null) {
            return;
        }

        double x = event.getX();
        double y = event.getY();

        // Make sure the pref width is up to date
        if (dragging.getPrefWidth() < 1.0) {
            dragging.setPrefWidth(dragging.getWidth());
            dragging.setPrefHeight(dragging.getHeight());
        }

        double xDiff = (previousX - x) * (dragPos == Position.LEFT ? -1 : 1);
        dragging.setPrefWidth(dragging.getPrefWidth() + xDiff);
        dragging.setPrefHeight(dragging.getPrefHeight() + (previousY - y));

        previousX = x;
        previousY = y;
    }

    protected void mouseOver(MouseEvent event) {
        if (getComponent(event, false) == null) {
            setCursor(Cursor.DEFAULT);
        } else {
            setCursor(Cursor.H_RESIZE); //TODO: this works only for 'left' and 'right'
        }
    }

    protected void mouseReleased(MouseEvent event) {
        if (dragging != null) {
            dragging.setPrefWidth(dragging.getWidth());
            dragging.setPrefHeight(dragging.getHeight());
        }
        dragging = null;
        dragPos = null;
        if (getComponent(event, false) == null) {
            setCursor(Cursor.DEFAULT);
        }
    }

    protected Region getComponent(MouseEvent event, boolean setPos) {
        double x = event.getX();
        Region left = (Region) getLeft();
        double leftX = left == null ? 0 : left.getWidth();
        Region right = (Region) getRight();
        double rightX = getWidth() - (right == null ? 0 : right.getWidth());

        if (0 < leftX - x && leftX - x < RESIZE_MARGIN) {
            if (setPos) {
                dragPos = Position.LEFT;
            }
            return left;
        }
        if (0 < x - rightX && x - rightX < RESIZE_MARGIN) {
            if (setPos) {
                dragPos = Position.RIGHT;
            }
            return right;
        }
        return null;
    }

    private enum Position {
        CENTER,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
    }

}
