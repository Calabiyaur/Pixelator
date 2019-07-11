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

    public BasicScrollPane() {
        super();
    }

    public BasicScrollPane(Node content) {
        super(content);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BasicScrollPaneSkin(this);
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
