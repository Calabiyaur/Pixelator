package main.java.control.parent;

import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.layout.StackPane;

import main.java.start.ExceptionHandler;
import main.java.util.ReflectionUtil;
import org.apache.commons.lang3.reflect.FieldUtils;

public class BasicScrollPaneSkin extends ScrollPaneSkin {

    StackPane viewRect_;
    StackPane corner_;

    public BasicScrollPaneSkin(BasicScrollPane control) {
        super(control);

        accessFields();
    }

    private void accessFields() {
        try {
            viewRect_ = (StackPane) FieldUtils.readField(this, "viewRect", true);
            corner_ = (StackPane) FieldUtils.readField(this, "corner", true);
        } catch (IllegalAccessException e) {
            ExceptionHandler.handle(e);
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset,
            double leftInset) {
        final BasicScrollPane sp = (BasicScrollPane) getSkinnable();

        double vsbWidth = (double) ReflectionUtil.invokeMethod(this, "computeVsbSizeHint", sp);
        double minWidth = vsbWidth + snappedLeftInset() + snappedRightInset();

        if (sp.getMinViewportWidth() > 0) {
            return (sp.getMinViewportWidth() + minWidth);
        } else {
            double w = corner_.minWidth(-1);
            return (w > 0) ? (3 * w) : 0;
        }
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset,
            double leftInset) {
        final BasicScrollPane sp = (BasicScrollPane) getSkinnable();

        double hsbHeight = (double) ReflectionUtil.invokeMethod(this, "computeHsbSizeHint", sp);
        double minHeight = hsbHeight + snappedTopInset() + snappedBottomInset();

        if (sp.getMinViewportHeight() > 0) {
            return (sp.getMinViewportHeight() + minHeight);
        } else {
            double h = corner_.minHeight(-1);
            return (h > 0) ? (3 * h) : 0;
        }
    }

    public StackPane getViewRect() {
        return viewRect_;
    }

    public StackPane getCorner() {
        return corner_;
    }
}
