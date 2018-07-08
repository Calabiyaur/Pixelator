package main.java.standard.control.basic;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import main.java.logging.Logger;

public class BasicScrollPane extends BorderPane {

    public static double SCROLL_BAR_WIDTH = 9;
    public static double SCROLL_BAR_HEIGHT = 8;

    private final BasicScrollBar hBar = new BasicScrollBar(true);
    private final BasicScrollBar vBar = new BasicScrollBar(false);
    private Region content;
    private boolean updating = false;

    private EventHandler<ScrollEvent> onRawScroll;

    public BasicScrollPane(Region content) {
        this();
        setContent(content);
    }

    public BasicScrollPane() {
        setRight(vBar);
        setBottom(hBar);
        hideBars();

        setStyle("-fx-background-color: #FFFFFF");

        hBar.translateXProperty().addListener((ov, o, n) -> {
            if (n.doubleValue() < getTranslateX()) {
                hBar.setTranslateX(getTranslateX());
            } else if (n.doubleValue() + hBar.getWidth() > getWidthOfCenter()) {
                hBar.setTranslateX(Math.max(getWidthOfCenter() - hBar.getWidth(), 0));
            }
            updateContentPosition();
        });
        vBar.translateYProperty().addListener((ov, o, n) -> {
            if (n.doubleValue() < getTranslateY()) {
                vBar.setTranslateY(getTranslateY());
            } else if (n.doubleValue() + vBar.getHeight() > getHeightOfCenter()) {
                vBar.setTranslateY(Math.max(getHeightOfCenter() - vBar.getHeight(), 0));
            }
            updateContentPosition();
        });

        widthProperty().addListener((ov, o, n) -> update());
        heightProperty().addListener((ov, o, n) -> update());

        setOnScroll(e -> {
            Logger.logEvent(e, "SCROLL");
            if (e.isControlDown()) {
                if (e.isShiftDown()) {
                    scrollHorizontally(e);
                } else {
                    scrollVertically(e);
                }
            } else {
                if (onRawScroll != null) {
                    onRawScroll.handle(e);
                }
            }
        });
    }

    /**
     * Set the scroll pane's content.
     */
    public void setContent(Region region) {
        content = region;
        content.widthProperty().addListener((ov, o, n) -> update());
        content.heightProperty().addListener((ov, o, n) -> update());

        Pane wrapper = new Pane(content);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty().subtract(vBar.widthProperty()));
        clip.heightProperty().bind(heightProperty().subtract(hBar.heightProperty()));
        wrapper.setClip(clip);

        setCenter(wrapper);
    }

    private void update() {
        if (updating) {
            return;
        }
        updating = true;
        Platform.runLater(() -> {
            updateBars();
            updateContentPosition();
            updating = false;
        });
    }

    private void updateBars() {
        hideBars();

        // See if scrolling horizontally is necessary
        double newWidth = getWidthOfCenter() * getWidthOfCenter() / content.getWidth();
        if (newWidth >= getWidthOfCenter()) {
            // h-scroll: NO
            newWidth = getWidthOfCenter();
        } else {
            // h-scroll: YES
            hBar.setMaxHeight(-1);
        }

        // See if scrolling vertically is necessary
        double newHeight = getHeightOfCenter() * getHeightOfCenter() / content.getHeight();
        if (newHeight >= getHeightOfCenter()) {
            // v-scroll: NO
            newHeight = getHeightOfCenter();
        } else {
            // v-scroll: YES
            vBar.setMaxWidth(-1);

            // See if scrolling horizontally became necessary because of the vertical scroll
            newWidth = getWidthOfCenter() * getWidthOfCenter() / content.getWidth();
            if (newWidth >= getWidthOfCenter()) {
                // h-scroll: NO
                hBar.setMaxHeight(0);
                newWidth = getWidthOfCenter();
            } else {
                // h-scroll: YES
                hBar.setMaxHeight(-1);
            }
        }

        hBar.setMaxWidth(newWidth);
        vBar.setMaxHeight(newHeight);

        // Make sure that the bars don't go outside of the window
        if (hBar.getTranslateX() + newWidth > getWidthOfCenter()) {
            hBar.setTranslateX(getWidthOfCenter() - newWidth);
        }
        if (vBar.getTranslateY() + newHeight > getHeightOfCenter()) {
            vBar.setTranslateY(getHeightOfCenter() - newHeight);
        }
    }

    private void updateContentPosition() {
        if (hBar.getMaxHeight() != 0) {
            double dWidth = getWidthOfCenter() - hBar.getWidth();
            double hBarX = hBar.getLayoutX() + hBar.getTranslateX();
            double xPercent = hBarX / (dWidth == 0 ? 1 : dWidth); // 0 = left, 1 = right
            content.setTranslateX(Math.round((content.getWidth() - getWidthOfCenter()) * (-xPercent)));
        } else {
            content.setTranslateX(0);
        }

        if (vBar.getMaxWidth() != 0) {
            double dHeight = getHeightOfCenter() - vBar.getHeight();
            double vBarY = vBar.getLayoutY() + vBar.getTranslateY();
            double yPercent = vBarY / (dHeight == 0 ? 1 : dHeight); // 0 = up, 1 = down
            content.setTranslateY(Math.round((content.getHeight() - getHeightOfCenter()) * (-yPercent)));
        } else {
            content.setTranslateY(0);
        }
    }

    private void hideBars() {
        hBar.setMaxHeight(0);
        vBar.setMaxWidth(0);
    }

    public Region getContent() {
        return content;
    }

    public BasicScrollBar getHBar() {
        return hBar;
    }

    public BasicScrollBar getVBar() {
        return vBar;
    }

    public double getContentX() {
        return content.getLayoutX();
    }

    public double getContentY() {
        return content.getLayoutY();
    }

    public double getContentX2() {
        return getContentX() + content.getWidth();
    }

    public double getContentY2() {
        return getContentY() + content.getHeight();
    }

    private double getWidthOfCenter() {
        if (isVerticalScroll()) {
            return getWidth() - vBar.getWidth();
        } else {
            return getWidth();
        }
    }

    private double getHeightOfCenter() {
        if (isHorizontalScroll()) {
            return getHeight() - hBar.getHeight();
        } else {
            return getHeight();
        }
    }

    private boolean isHorizontalScroll() {
        return hBar.getMaxHeight() != 0;
    }

    private boolean isVerticalScroll() {
        return vBar.getMaxWidth() != 0;
    }

    public void translateContent(double h, double v) { //FIXME
        double hFactor = (getWidthOfCenter() - hBar.getWidth()) / content.getWidth();
        double vFactor = (getHeightOfCenter() - vBar.getHeight()) / content.getHeight();
        hBar.setTranslateX(hBar.getTranslateX() + h * hFactor);
        vBar.setTranslateY(vBar.getTranslateY() + v * vFactor);
    }

    public void setScrollByMouse(boolean scrollByMouse) {
        if (scrollByMouse) {
            setOnRawScroll(e -> scrollVertically(e));
        } else {
            setOnRawScroll(onRawScroll);
        }
    }

    private void scrollHorizontally(ScrollEvent e) {
        hBar.setTranslateX(hBar.getTranslateX() - e.getDeltaX());
    }

    private void scrollVertically(ScrollEvent e) {
        vBar.setTranslateY(vBar.getTranslateY() - e.getDeltaY());
    }

    public void setOnRawScroll(EventHandler<ScrollEvent> value) {
        onRawScroll = value;
    }

}
