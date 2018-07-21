package main.java.control.parent;

import java.util.Collections;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import main.java.util.GridPaneUtil;

public class ResizableGridPane extends GridPane {

    private double previousX = 0;
    private double previousY = 0;
    private ColumnConstraints hDrag;
    private RowConstraints vDrag;

    public ResizableGridPane() {
        setOnMousePressed(event -> mousePressed(event));
        setOnMouseDragged(event -> mouseDragged(event));
        setOnMouseMoved(event -> mouseOver(event));
        setOnMouseReleased(event -> mouseReleased(event));
        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                for (Node node : c.getAddedSubList()) {
                    Integer columnIndex = getColumnIndex(node);
                    if (columnIndex != null) {
                        createColumnConstraintsUpTo(columnIndex);
                    }
                    Integer rowIndex = getRowIndex(node);
                    if (rowIndex != null) {
                        createRowConstraintsUpTo(rowIndex);
                    }
                }
            }
        });
    }

    private void mousePressed(MouseEvent event) {
        hDrag = getHdrag(event, true);
        if (hDrag != null) {
            previousX = event.getX();
        }

        vDrag = getVdrag(event, true);
        if (vDrag != null) {
            previousY = event.getY();
        }
    }

    private void mouseDragged(MouseEvent event) {
        if (hDrag != null) {
            double x = event.getX();

            double xDiff = (previousX - x);
            double newWidth = hDrag.getPrefWidth() - xDiff;
            hDrag.setPrefWidth(newWidth);
            hDrag.setMinWidth(newWidth);
            hDrag.setMaxWidth(newWidth);

            previousX = x;
        }

        if (vDrag != null) {
            double y = event.getY();

            double yDiff = (previousY - y);
            double newHeight = vDrag.getPrefHeight() - yDiff;
            vDrag.setPrefHeight(newHeight);
            vDrag.setMinHeight(newHeight);
            vDrag.setMaxHeight(newHeight);

            previousY = y;
        }
    }

    private void mouseOver(MouseEvent event) {
        boolean h = getHdrag(event, false) != null;
        boolean v = getVdrag(event, false) != null;
        if (!h && !v) {
            setCursor(Cursor.DEFAULT);
        } else if (h && !v){
            setCursor(Cursor.H_RESIZE);
        } else if (!h){
            setCursor(Cursor.V_RESIZE);
        } else {
            setCursor(Cursor.CROSSHAIR);
        }
    }

    private void mouseReleased(MouseEvent event) {
        hDrag = null;
        if (getHdrag(event, false) == null && getVdrag(event, false) == null) {
            setCursor(Cursor.DEFAULT);
        }
    }

    private ColumnConstraints getHdrag(MouseEvent event, boolean init) {
        double x = 0;
        int i = 0;
        for (Double width : GridPaneUtil.getWidthOfChildren(this)) {
            x += width;
            if (x <= event.getX() && event.getX() < x + getHgap()) {
                ColumnConstraints cc = getColumnConstraints().get(i);
                if (init) {
                    cc.setPrefWidth(width);
                }
                return cc;
            }
            i++;
            x += getHgap();
        }
        return null;
    }

    private RowConstraints getVdrag(MouseEvent event, boolean init) {
        double y = 0;
        int i = 0;
        for (Double height : GridPaneUtil.getHeightOfChildren(this)) {
            y += height;
            if (y <= event.getY() && event.getY() < y + getVgap()) {
                RowConstraints rc = getRowConstraints().get(i);
                if (init) {
                    rc.setPrefHeight(height);
                }
                return rc;
            }
            i++;
            y += getVgap();
        }
        return null;
    }

    public void setPrefWidth(int columnIndex, double prefWidth) {
        getColumnConstraints().get(columnIndex).setPrefWidth(prefWidth);
    }

    public void setPrefHeight(int rowIndex, double prefHeight) {
        getRowConstraints().get(rowIndex).setPrefHeight(prefHeight);
    }

    public void setHgrow(int columnIndex, Priority priority) {
        getColumnConstraints().get(columnIndex).setHgrow(priority);
    }

    public void setVgrow(int rowIndex, Priority priority) {
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
