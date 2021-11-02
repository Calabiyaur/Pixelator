package com.calabi.pixelator.ui.parent;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.util.NumberUtil;

public abstract class DraggablePane extends GridPane {

    public static final int RESIZE_MARGIN = 6;

    GridPane content;

    private double previousX;
    private double previousY;
    private boolean dragging;

    private DoubleProperty minX = new SimpleDoubleProperty(Double.MIN_VALUE); //FIXME: Min / Max values work only half
    private DoubleProperty minY = new SimpleDoubleProperty(Double.MIN_VALUE);
    private DoubleProperty maxX = new SimpleDoubleProperty(Double.MAX_VALUE);
    private DoubleProperty maxY = new SimpleDoubleProperty(Double.MAX_VALUE);

    private boolean dragHorizontal = true;
    private boolean dragVertical = true;

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
        setOnMouseReleased(event -> mouseReleased(event));
    }

    void createBorder() {
        super.add(new BorderRegion(this, Cursor.NW_RESIZE, RESIZE_MARGIN), 0, 0);
        super.add(new BorderRegion(this, Cursor.N_RESIZE, RESIZE_MARGIN), 1, 0);
        super.add(new BorderRegion(this, Cursor.NE_RESIZE, RESIZE_MARGIN), 2, 0);
        super.add(new BorderRegion(this, Cursor.E_RESIZE, RESIZE_MARGIN), 2, 1);
        super.add(new BorderRegion(this, Cursor.SE_RESIZE, RESIZE_MARGIN), 2, 2);
        super.add(new BorderRegion(this, Cursor.S_RESIZE, RESIZE_MARGIN), 1, 2);
        super.add(new BorderRegion(this, Cursor.SW_RESIZE, RESIZE_MARGIN), 0, 2);
        super.add(new BorderRegion(this, Cursor.W_RESIZE, RESIZE_MARGIN), 0, 1);
    }

    private void mousePressed(MouseEvent event) {
        requestFocus();

        double x = event.getX();
        double y = event.getY();

        if (MouseButton.PRIMARY.equals(event.getButton())
                && x >= 0 && x < getWidth()
                && y >= 0 && y < getHeight()) {

            dragging = true;

            previousX = x;
            previousY = y;
        }
    }

    private void mouseDragged(MouseEvent event) {
        if (dragging && MouseButton.PRIMARY.equals(event.getButton())) {
            double x = event.getX();
            double y = event.getY();

            if (dragHorizontal) {
                double xDiff = x - previousX;
                setTranslateX(getTranslateX() + xDiff);
            }
            if (dragVertical) {
                double yDiff = y - previousY;
                setTranslateY(Math.max(minY.doubleValue(), getTranslateY() + yDiff));
            }
        }
    }

    private void mouseReleased(MouseEvent event) {
        if (MouseButton.PRIMARY.equals(event.getButton())) {
            dragging = false;
        }
    }

    protected void resetPosition(double width, double height) {
        setTranslateX(NumberUtil.minMax(minX.get(), getTranslateX(), maxX.get() - width));
        setTranslateY(NumberUtil.minMax(minY.get(), getTranslateY(), maxY.get() - height));
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

    public void remove(Node child) {
        content.getChildren().remove(child);
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

    /**
     * Border region of the draggable pane that allows for resizing.
     */
    public static class BorderRegion extends Region {

        private final Direction resizeDir;
        private boolean resizing;

        private double prevX;
        private double prevY;

        public BorderRegion(DraggablePane origin, Cursor cursor, int size) {
            this(origin, cursor, size, origin, origin);
        }

        public BorderRegion(DraggablePane origin, Cursor cursor, int size, Region hSubject, Region vSubject) {

            resizeDir = Direction.getDirection(cursor);

            setCursor(cursor);
            setMinSize(size, size);

            setOnMousePressed(event -> {
                if (MouseButton.PRIMARY.equals(event.getButton())) {
                    startResize(event);
                }
            });
            setOnMouseDragged(event -> {
                if (resizing) {
                    resize(origin, event, hSubject, vSubject);
                }
            });
            setOnMouseReleased(event -> resizing = false);
        }

        private void startResize(MouseEvent event) {
            resizing = true;
            prevX = event.getSceneX();
            prevY = event.getSceneY();
            event.consume();
        }

        private void resize(DraggablePane origin, MouseEvent event, Region hSubject, Region vSubject) {
            double x = event.getSceneX();
            double y = event.getSceneY();

            // Make sure the pref size is up to date
            if (hSubject.getPrefWidth() < 1.0) {
                hSubject.setPrefWidth(hSubject.getWidth());
            }
            if (vSubject.getPrefHeight() < 1.0) {
                vSubject.setPrefHeight(vSubject.getHeight());
            }

            double xDiff = resizeDir.isEast() || resizeDir.isWest() ? x - prevX : 0;
            double yDiff = resizeDir.isNorth() || resizeDir.isSouth() ? y - prevY : 0;

            if (resizeDir.isEast()) {
                origin.setPrefWidth(origin.getPrefWidth() + xDiff);
                if (hSubject != origin) {
                    hSubject.setPrefWidth(hSubject.getPrefWidth() + xDiff);
                }
            } else if (resizeDir.isWest()) {
                hSubject.setTranslateX(hSubject.getTranslateX() + xDiff);
                hSubject.setPrefWidth(hSubject.getPrefWidth() - xDiff);
                if (hSubject != origin) {
                    hSubject.setPrefWidth(hSubject.getPrefWidth() - xDiff);
                }
            }

            if (resizeDir.isSouth()) {
                origin.setPrefHeight(origin.getPrefHeight() + yDiff);
                if (vSubject != origin) {
                    vSubject.setPrefHeight(vSubject.getPrefHeight() + yDiff);
                }
            } else if (resizeDir.isNorth()) {
                vSubject.setTranslateY(vSubject.getTranslateY() + yDiff);
                origin.setPrefHeight(origin.getPrefHeight() - yDiff);
                if (vSubject != origin) {
                    vSubject.setPrefHeight(vSubject.getPrefHeight() - yDiff);
                }
            }

            prevX = x;
            prevY = y;

            event.consume();
        }

    }

}
