package com.calabi.pixelator.control.parent;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import com.calabi.pixelator.util.ReflectionUtil;

public class BasicScrollPane extends ScrollPane {

    public static final double BAR_BREADTH = 8.;

    private boolean scrollByMouse = false;
    private EventHandler<ScrollEvent> onRawScroll;

    public BasicScrollPane(Region content) {
        this();
        setContent(content);
    }

    public BasicScrollPane() {
        skinProperty().addListener((o, v, n) -> {
            if (n != null) {
                initialize();
            }
        });
    }

    private void initialize() {
        ScrollPaneSkin skin = (ScrollPaneSkin) getSkin();

        // Disable scrolling from scroll pane content
        StackPane viewRect = ReflectionUtil.getField(skin, "viewRect");
        viewRect.addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));

        // Disable scrolling from scroll bars
        skin.getHorizontalScrollBar().addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));
        skin.getVerticalScrollBar().addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));

        // Set scroll bar size
        skin.getVerticalScrollBar().setPrefWidth(BAR_BREADTH);
        skin.getHorizontalScrollBar().setPrefHeight(BAR_BREADTH);
    }

    private void filterScrolling(ScrollEvent e) {
        if (!e.isControlDown()) {
            if (onRawScroll != null) {
                onRawScroll.handle(e);
            }
            if (!scrollByMouse) {
                e.consume();
            }
        }
    }

    public void setScrollByMouse(boolean value) {
        this.scrollByMouse = value;
    }

    public void setOnRawScroll(EventHandler<ScrollEvent> value) {
        onRawScroll = value;
    }

}
