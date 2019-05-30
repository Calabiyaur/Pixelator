package main.java.control.parent;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;

import main.java.start.ExceptionHandler;

public class BasicScrollPane extends ScrollPane {

    private boolean scrollByMouse = false;
    private EventHandler<ScrollEvent> onRawScroll;

    public BasicScrollPane(Region content) {
        this();
        setContent(content);
    }

    public BasicScrollPane() {
        skinProperty().addListener((o, v, n) -> {
            if (n != null) {
                try {
                    initialize();
                } catch (IllegalAccessException e) {
                    ExceptionHandler.handle(e);
                }
            }
        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BasicScrollPaneSkin(this);
    }

    private void initialize() throws IllegalAccessException {
        BasicScrollPaneSkin skin = (BasicScrollPaneSkin) getSkin();

        // Disable scrolling from scroll pane content
        skin.getViewRect().addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));

        // Disable scrolling from scroll bars
        skin.getHorizontalScrollBar().addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));
        skin.getVerticalScrollBar().addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));
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
