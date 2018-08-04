package main.java.control.parent;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ResizableBorderPane extends VBox {

    private SplitPane splitPane;
    private Node top = new Pane();
    private Node left = new Pane();
    private Node right = new Pane();
    private Node center = new Pane();
    private Node bottom = new Pane();

    public ResizableBorderPane() {
        splitPane = new SplitPane();

        getChildren().setAll(top, splitPane, bottom);
        splitPane.getItems().setAll(left, center, right);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
    }

    public void setLeftMargin(double margin) {
        splitPane.getDividers().get(0).setPosition(margin / splitPane.getWidth());
    }

    public void setRightMargin(double margin) {
        splitPane.getDividers().get(1).setPosition(1 - margin / splitPane.getWidth());
    }

    public Node getTop() {
        return top;
    }

    public void setTop(Node top) {
        this.top = top;
        getChildren().set(0, top);
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
        splitPane.getItems().set(0, left);
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        if (right != null) {
            splitPane.getItems().set(2, right);
        } else {
            splitPane.getItems().remove(2);
        }
        this.right = right;
    }

    public Node getCenter() {
        return center;
    }

    public void setCenter(Node center) {
        this.center = center;
        splitPane.getItems().set(1, center);
    }

    public Node getBottom() {
        return bottom;
    }

    public void setBottom(Node bottom) {
        this.bottom = bottom;
        getChildren().set(2, bottom);
    }
}
