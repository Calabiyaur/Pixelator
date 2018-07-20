package main.java.control.parent;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import main.java.meta.Direction;
import main.java.util.NumberUtil;

public abstract class DraggablePane extends GridPane {

    private double previousX = 0;
    private double previousY = 0;
    private boolean dragging;
    private Direction resizing;

    private DoubleProperty minX = new SimpleDoubleProperty(Double.MIN_VALUE); //FIXME: Min / Max values work only half
    private DoubleProperty minY = new SimpleDoubleProperty(Double.MIN_VALUE);
    private DoubleProperty maxX = new SimpleDoubleProperty(Double.MAX_VALUE);
    private DoubleProperty maxY = new SimpleDoubleProperty(Double.MAX_VALUE);

    private boolean dragHorizontal = true;
    private boolean dragVertical = true;
    private boolean resizable = true;

    ObservableList<EventHandler<? super MouseEvent>> onMouseMoved = FXCollections.observableArrayList();
    ObservableList<EventHandler<? super MouseEvent>> onMouseDragged = FXCollections.observableArrayList();

    public DraggablePane() {
        getStyleClass().addAll("draggable-pane");
        setPickOnBounds(true);
        setOnMousePressed(event -> mousePressed(event));
        setOnMouseDragged(event -> mouseDragged(event));
        setOnMouseMoved(event -> mouseOver(event));
        setOnMouseReleased(event -> mouseReleased(event));
    }

    public void resetCursor() {
        setCursor(Cursor.DEFAULT);
    }

    protected void mousePressed(MouseEvent event) {
        setFocused(false);
        setFocused(true);

        dragging = isDraggableHere(event);
        if (!dragging) {
            resizing = isResizableHere(event);
            if (resizing == null) {
                return;
            }
        }

        previousX = event.getX();
        previousY = event.getY();

        if (dragging) {

        } else {

        }
    }

    protected void mouseDragged(MouseEvent event) {
        if (dragging || resizing != null) {
            double x = event.getX();
            double y = event.getY();

            // Make sure the pref size is up to date
            if (getPrefWidth() < 1.0) {
                setPrefWidth(getWidth());
                setPrefHeight(getHeight());
            }

            if (dragging) {
                if (dragHorizontal) {
                    double xDiff = x - previousX;
                    setTranslateX(getTranslateX() + xDiff);
                }
                if (dragVertical) {
                    double yDiff = y - previousY;
                    setTranslateY(getTranslateY() + yDiff);
                }
            } else if (resizable) {
                double xDiff = resizing.isEast() || resizing.isWest() ? x - previousX : 0;
                double yDiff = resizing.isNorth() || resizing.isSouth() ? y - previousY : 0;

                if (resizing.isEast()) {
                    setPrefWidth(getPrefWidth() + xDiff);
                    previousX = x;
                } else if (resizing.isWest()) {
                    setTranslateX(getTranslateX() + xDiff);
                    setPrefWidth(getPrefWidth() - xDiff);
                }

                if (resizing.isSouth()) {
                    setPrefHeight(getPrefHeight() + yDiff);
                    previousY = y;
                } else if (resizing.isNorth()) {
                    setTranslateY(getTranslateY() + yDiff);
                    setPrefHeight(getPrefHeight() - yDiff);
                }
            }
        }

        onMouseDragged.forEach(c -> c.handle(event));
    }

    protected void resetPosition(double width, double height) {
        setTranslateX(NumberUtil.minMax(minX.get(), getTranslateX(), maxX.get() - width));
        setTranslateY(NumberUtil.minMax(minY.get(), getTranslateY(), maxY.get() - height));
    }

    protected void mouseOver(MouseEvent event) {
        Direction dir = isResizableHere(event);
        if (dir == null) {
            resetCursor();
        } else {
            switch(dir) {
                case EAST:
                case WEST:
                    setCursor(Cursor.E_RESIZE);
                    break;
                case NORTH_EAST:
                case SOUTH_WEST:
                    setCursor(Cursor.NE_RESIZE);
                    break;
                case NORTH:
                case SOUTH:
                    setCursor(Cursor.S_RESIZE);
                    break;
                case NORTH_WEST:
                case SOUTH_EAST:
                    setCursor(Cursor.SE_RESIZE);
                    break;
                default:
                    resetCursor();
                    break;
            }
        }

        onMouseMoved.forEach(c -> c.handle(event));
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        resizing = null;
        if (isResizableHere(event) == null) {
            resetCursor();
        }
    }

    public void addOnMouseMoved(EventHandler<? super MouseEvent> value) {
        onMouseMoved.add(value);
    }

    public void addOnMouseDragged(EventHandler<? super MouseEvent> value) {
        onMouseDragged.add(value);
    }

    public double getMinX() {
        return minX.get();
    }

    public DoubleProperty minXProperty() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX.set(minX);
    }

    public double getMinY() {
        return minY.get();
    }

    public DoubleProperty minYProperty() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY.set(minY);
    }

    public double getMaxX() {
        return maxX.get();
    }

    public DoubleProperty maxXProperty() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX.set(maxX);
    }

    public double getMaxY() {
        return maxY.get();
    }

    public DoubleProperty maxYProperty() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY.set(maxY);
    }

    public abstract boolean isDraggableHere(MouseEvent event);

    public abstract Direction isResizableHere(MouseEvent event);

    public boolean isDragHorizontal() {
        return dragHorizontal;
    }

    public void setDragHorizontal(boolean dragHorizontal) {
        this.dragHorizontal = dragHorizontal;
    }

    public boolean isDragVertical() {
        return dragVertical;
    }

    public void setDragVertical(boolean dragVertical) {
        this.dragVertical = dragVertical;
    }

    @Override public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }
}
