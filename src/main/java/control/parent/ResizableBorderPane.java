package main.java.control.parent;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ResizableBorderPane extends VBox {

    private SplitPane splitPane;
    private Node top;
    private Node left;
    private Node right;
    private Node center;
    private Node bottom;

    public ResizableBorderPane() {
        splitPane = new SplitPane();

        getChildren().setAll(new Pane(), splitPane, new Pane());
        splitPane.getItems().setAll(new Pane(), new Pane(), new Pane());
        VBox.setVgrow(splitPane, Priority.ALWAYS);
    }

    public Node getTop() {
        return top;
    }

    public void setTop(Pane top) {
        this.top = top;
        getChildren().set(0, top);
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Pane left) {
        this.left = left;
        splitPane.getItems().set(0, left);
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Pane right) {
        this.right = right;
        splitPane.getItems().set(2, right);
    }

    public Node getCenter() {
        return center;
    }

    public void setCenter(Pane center) {
        this.center = center;
        splitPane.getItems().set(1, center);
    }

    public Node getBottom() {
        return bottom;
    }

    public void setBottom(Pane bottom) {
        this.bottom = bottom;
        getChildren().set(2, bottom);
    }
}
