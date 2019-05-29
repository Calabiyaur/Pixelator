package main.java.control.parent;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import main.java.start.ExceptionHandler;
import org.apache.commons.lang3.reflect.FieldUtils;

public class BasicScrollPane extends ScrollPane {

    private ScrollBar hBar;
    private ScrollBar vBar;

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

    private void initialize() throws IllegalAccessException {
        // Disable scrolling from scroll pane content
        Object viewRect = FieldUtils.readField(getSkin(), "viewRect", true);
        ((StackPane) viewRect).addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));

        // Disable scrolling from scroll bars
        Object hsb = FieldUtils.readField(getSkin(), "hsb", true);
        hBar = ((ScrollBar) hsb);
        hBar.addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));

        Object vsb = FieldUtils.readField(getSkin(), "vsb", true);
        vBar = ((ScrollBar) vsb);
        vBar.addEventFilter(ScrollEvent.SCROLL, event -> filterScrolling(event));


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
