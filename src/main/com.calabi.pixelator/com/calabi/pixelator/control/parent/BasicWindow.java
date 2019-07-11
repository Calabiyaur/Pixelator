package com.calabi.pixelator.control.parent;

import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.res.Images;

public class BasicWindow extends DraggablePane {

    private static final int HEADER_HEIGHT = 21;

    private final Label text = new Label("");
    private final HBox buttonBox = new HBox();
    private final ImageButton close = new ImageButton(Images.CLOSE);
    private final BasicScrollPane innerContent = new BasicScrollPane();
    private EventHandler<Event> onClose;

    public BasicWindow(boolean showTitle) {
        if (showTitle) {
            add(text, 1, 2);
            GridPane.setValignment(text, VPos.TOP);
            GridPane.setMargin(text, new Insets(0, 0, 0, 1));
            add(buttonBox, 2, 1, 2, 2);
            buttonBox.getChildren().add(close);
            buttonBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }
        add(innerContent, 1, 3);

        setHgrow(text, Priority.ALWAYS);
        setHalignment(buttonBox, HPos.RIGHT);
        setVgrow(innerContent, Priority.ALWAYS);

        initStyle();
    }

    @Override
    void createBorder() {
        int normal = RESIZE_MARGIN;
        int small = 1;
        int reduced = normal - small;

        add(this.createBorderRegion(Cursor.NW_RESIZE, reduced), 0, 0, 1, 2);
        add(this.createBorderRegion(Cursor.N_RESIZE, reduced), 1, 0, 1, 2);
        add(this.createBorderRegion(Cursor.N_RESIZE, small), 2, 0, 2, 1);
        add(this.createBorderRegion(Cursor.NE_RESIZE, small), 4, 0);
        add(this.createBorderRegion(Cursor.E_RESIZE, small), 4, 1, 1, 2);
        add(this.createBorderRegion(Cursor.E_RESIZE, reduced), 3, 3, 2, 1);
        add(this.createBorderRegion(Cursor.SE_RESIZE, reduced), 3, 4, 2, 1);
        add(this.createBorderRegion(Cursor.S_RESIZE, normal), 1, 4, 2, 1);
        add(this.createBorderRegion(Cursor.SW_RESIZE, normal), 0, 4);
        add(this.createBorderRegion(Cursor.W_RESIZE, normal), 0, 2, 1, 2);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setMinWidth(reduced);
        column3.setMaxWidth(reduced);
        content.getColumnConstraints().addAll(
                new ColumnConstraints(),
                new ColumnConstraints(),
                new ColumnConstraints(),
                column3,
                new ColumnConstraints()
        );

        RowConstraints row0 = new RowConstraints();
        row0.setMinHeight(small);
        row0.setMaxHeight(small);
        RowConstraints row1 = new RowConstraints();
        row1.setMinHeight(reduced);
        row1.setMaxHeight(reduced);
        RowConstraints row2 = new RowConstraints();
        row2.setMinHeight(HEADER_HEIGHT);
        row2.setMaxHeight(HEADER_HEIGHT);
        content.getRowConstraints().addAll(
                row0,
                row1,
                row2,
                new RowConstraints(),
                new RowConstraints()
        );
    }

    private void initStyle() {
        text.getStyleClass().setAll("text");
        close.getStyleClass().setAll("close-button");
    }

    protected void addButton(Node button) {
        buttonBox.getChildren().add(button);
        button.getStyleClass().setAll("default-button");
    }

    public void setOnCloseRequest(EventHandler<Event> e) {
        this.onClose = e;
    }

    public void close() {
        onClose.handle(new Event(Event.ANY));
    }

    public void setContent(Region graphic) {
        innerContent.setContent(graphic);
    }

    public double getHeaderHeight() {
        return HEADER_HEIGHT;
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
        return innerContent;
    }
}
