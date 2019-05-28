package main.java.control.parent;

import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import main.java.control.basic.ImageButton;
import main.java.res.Images;

public class BasicWindow extends DraggablePane {

    private Label text = new Label("");
    private HBox buttonBox = new HBox();
    private ImageButton close = new ImageButton(Images.CLOSE);
    private BasicScrollPane content = new BasicScrollPane();
    private EventHandler<Event> onClose;

    public BasicWindow(boolean showTitle) {
        if (showTitle) {
            int margin = 0; //TODO: RESIZE_MARGIN - 1;
            add(text, 0, 0);
            text.setTranslateX(-margin);
            text.setTranslateY(-margin);
            add(buttonBox, 1, 0);
            buttonBox.getChildren().add(close);
            buttonBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            buttonBox.setTranslateX(margin);
            buttonBox.setTranslateY(-margin);
        }
        HBox contentBox = new HBox(content);
        VBox wrapper = new VBox(contentBox);
        wrapper.setAlignment(Pos.CENTER);
        contentBox.setAlignment(Pos.CENTER);
        add(wrapper, 0, 1, 2, 1);

        GridPane.setHgrow(text, Priority.ALWAYS);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);
        GridPane.setVgrow(wrapper, Priority.ALWAYS);

        initStyle();
    }

    private void initStyle() {
        text.getStyleClass().add("text");
        close.getStyleClass().add("close-button");
    }

    protected void addButton(Node button) {
        buttonBox.getChildren().add(button);
        button.getStyleClass().add("default-button");
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
