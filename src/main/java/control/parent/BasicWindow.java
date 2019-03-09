package main.java.control.parent;

import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import main.java.control.basic.ImageButton;
import main.java.meta.Direction;
import main.java.res.Images;

public class BasicWindow extends DraggablePane {

    public static final int RESIZE_MARGIN = 6;
    private Label text = new Label("");
    private HBox buttonBox = new HBox();
    private ImageButton close = new ImageButton(Images.CLOSE);
    private BasicScrollPane content = new BasicScrollPane();
    private EventHandler<Event> onClose;

    public BasicWindow(boolean showTitle) {
        if (showTitle) {
            add(text, 0, 0);
            add(buttonBox, 1, 0);
            buttonBox.getChildren().add(close);
            buttonBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }
        add(content, 0, 1, 2, 1);
        GridPane.setMargin(content, new Insets(RESIZE_MARGIN));

        GridPane.setHgrow(text, Priority.ALWAYS);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);
        GridPane.setVgrow(content, Priority.ALWAYS);

        initStyle();
    }

    private void initStyle() {
        text.getStyleClass().add("text");
        text.setOnMouseMoved(e -> resetCursor());
        close.getStyleClass().add("close-button");
        close.setOnMouseMoved(e -> resetCursor());
    }

    public boolean isDraggableHere(MouseEvent event) {
        if (isScrollBarHere(event)) {
            // If a scroll bar is here, don't drag.
            return false;
        }
        if (isOutOfBounds(event)) {
            return false;
        }
        // If mouse is outside of the content, drag. (If not resizable here)
        return !isContentHere(event) && isResizableHere(event) == null;
    }

    protected boolean isContentHere(MouseEvent event) {
        double x = content.getContentX() + content.getLayoutX();
        double y = content.getContentY() + content.getLayoutY();
        double x2 = content.getContentX2() + content.getLayoutX();
        double y2 = content.getContentY2() + content.getLayoutY();
        return event.getX() >= x && event.getX() <= x2 && event.getY() >= y && event.getY() <= y2;
    }

    @Override public Direction isResizableHere(MouseEvent event) {
        if (!isResizable() || isScrollBarHere(event) || isOutOfBounds(event)) {
            return null;
        }
        double x = getLayoutX();
        double y = getLayoutY();
        double width = getWidth();
        double height = getHeight();

        boolean east = Math.abs(x + width - event.getX()) < RESIZE_MARGIN;
        boolean north = Math.abs(y - event.getY()) < RESIZE_MARGIN;
        boolean west = Math.abs(x - event.getX()) < RESIZE_MARGIN;
        boolean south = Math.abs(y + height - event.getY()) < RESIZE_MARGIN;

        return Direction.getDirection(east, north, west, south);
    }

    private boolean isOutOfBounds(MouseEvent event) {
        // If mouse is outside of the window, don't drag.
        return event.getX() < getLayoutX() || event.getX() > getLayoutX() + getWidth()
                || event.getY() < getLayoutY() || event.getY() > getLayoutY() + getHeight();
    }

    private boolean isScrollBarHere(MouseEvent event) {
        BasicScrollBar vBar = content.getVBar();
        BasicScrollBar hBar = content.getHBar();

        return (event.getX() >= content.getLayoutX() + vBar.getLayoutX()
                && event.getX() <= content.getLayoutX() + vBar.getLayoutX() + vBar.getWidth())
                || (event.getY() >= content.getLayoutY() + hBar.getLayoutY()
                && event.getY() <= content.getLayoutY() + hBar.getLayoutY() + hBar.getHeight());
    }

    protected void addButton(Node button) {
        buttonBox.getChildren().add(button);
        button.getStyleClass().add("default-button");
        button.setOnMouseMoved(e -> resetCursor());
    }

    public void setOnCloseRequest(EventHandler<Event> e) {
        this.onClose = e;
    }

    public void close() {
        onClose.handle(new Event(Event.ANY));
    }

    public void setContent(Region graphic) {
        content.setContent(graphic);
    }

    public double getHeaderHeight() {
        return buttonBox.getHeight() == 0 ? 26 : buttonBox.getHeight();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public String getText() {
        return this.text.getText();
    }

    public StringProperty textProperty() {
        return this.text.textProperty();
    }

    public ImageButton getClose() {
        return close;
    }

    public BasicScrollPane getContent() {
        return content;
    }
}
