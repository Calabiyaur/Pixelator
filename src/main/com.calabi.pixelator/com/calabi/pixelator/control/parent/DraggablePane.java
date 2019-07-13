package com.calabi.pixelator.control.parent;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.util.NumberUtil;

public abstract class DraggablePane extends GridPane {

    public static final int RESIZE_MARGIN = 6;

    GridPane content;

    private double previousX = 0;
    private double previousY = 0;
    private boolean dragging;
    private Direction resizing;
    private boolean exiting;

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
        getStyleClass().setAll("draggable-pane");
        setPickOnBounds(true);

        content = new GridPane();
        super.add(content, 1, 1);
        createBorder();
        GridPane.setVgrow(content, Priority.ALWAYS);
        GridPane.setHgrow(content, Priority.ALWAYS);

        setOnMousePressed(event -> mousePressed(event));
        setOnMouseDragged(event -> mouseDragged(event));
        setOnMouseMoved(event -> mouseOver(event));
        setOnMouseReleased(event -> mouseReleased(event));
    }

    void createBorder() {
        super.add(this.createBorderRegion(Cursor.NW_RESIZE, RESIZE_MARGIN), 0, 0);
        super.add(this.createBorderRegion(Cursor.N_RESIZE, RESIZE_MARGIN), 1, 0);
        super.add(this.createBorderRegion(Cursor.NE_RESIZE, RESIZE_MARGIN), 2, 0);
        super.add(this.createBorderRegion(Cursor.E_RESIZE, RESIZE_MARGIN), 2, 1);
        super.add(this.createBorderRegion(Cursor.SE_RESIZE, RESIZE_MARGIN), 2, 2);
        super.add(this.createBorderRegion(Cursor.S_RESIZE, RESIZE_MARGIN), 1, 2);
        super.add(this.createBorderRegion(Cursor.SW_RESIZE, RESIZE_MARGIN), 0, 2);
        super.add(this.createBorderRegion(Cursor.W_RESIZE, RESIZE_MARGIN), 0, 1);
    }

    final Region createBorderRegion(Cursor cursor, int size) {
        Region region = new Region();
        region.setMinSize(size, size);
        region.setOnMouseEntered(event -> {
            setCursor(cursor);
            exiting = false;
        });
        region.setOnMouseExited(event -> {
            if (resizing == null) {
                setCursor(Cursor.DEFAULT);
            } else {
                exiting = true;
            }
        });
        return region;
    }

    protected void mousePressed(MouseEvent event) {
        requestFocus();

        resizing = Direction.getDirection(getCursor());
        if (resizing == Direction.NONE) {
            dragging = true;
        }

        previousX = event.getX();
        previousY = event.getY();
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
                    setTranslateY(Math.max(minY.doubleValue(), getTranslateY() + yDiff));
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
        onMouseMoved.forEach(c -> c.handle(event));
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        resizing = null;
        if (exiting) {
            exiting = false;
            setCursor(Cursor.DEFAULT);
        }
    }

    @Override
    public void add(Node child, int columnIndex, int rowIndex) {
        content.add(child, columnIndex, rowIndex);
    }

    @Override
    public void add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan) {
        content.add(child, columnIndex, rowIndex, colspan, rowspan);
    }

    @Override
    public void addRow(int rowIndex, Node... children) {
        content.addRow(rowIndex, children);
    }

    @Override
    public void addColumn(int columnIndex, Node... children) {
        content.addColumn(columnIndex, children);
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
