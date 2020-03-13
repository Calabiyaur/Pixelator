package com.calabi.pixelator.control.parent;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.input.ScrollEvent;

public class BasicScrollPane extends ScrollPane {

    public static final double BAR_BREADTH = 8.;

    private boolean scrollByMouse = false;
    private EventHandler<ScrollEvent> onRawScroll;

    private boolean centerContent = true;

    public BasicScrollPane() {
        this(null);
    }

    public BasicScrollPane(Node content) {
        super(content);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BasicScrollPaneSkin(this);
    }

    public boolean isScrollByMouse() {
        return scrollByMouse;
    }

    public void setScrollByMouse(boolean value) {
        this.scrollByMouse = value;
    }

    public boolean isCenterContent() {
        return centerContent;
    }

    public void setCenterContent(boolean centerContent) {
        this.centerContent = centerContent;
    }

    public EventHandler<ScrollEvent> getOnRawScroll() {
        return onRawScroll;
    }

    public void setOnRawScroll(EventHandler<ScrollEvent> value) {
        onRawScroll = value;
    }

}
