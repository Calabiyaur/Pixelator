package main.java.standard.image;

import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;

import org.apache.commons.lang3.math.Fraction;

public class ScalableImageView extends PixelatedImageView {

    private static final double ZOOM_MINIMUM = 0.1;
    private static final double ZOOM_MAXIMUM = 32;

    public ScalableImageView(Image image) {
        super(image);
    }

    public void scroll(ScrollEvent e) {
        double deltaY = e.getDeltaY();

        if (!(e.isControlDown() || e.isShiftDown() || e.isAltDown())) {
            if (deltaY > 0) {
                zoomIn();
            } else {
                zoomOut();
            }
        }
    }

    public void zoomIn() {
        Fraction zoomFactor = Fraction.getFraction(getScaleX());
        Fraction newZoom = zoomFactor.doubleValue() >= 1
                ? zoomFactor.add(Fraction.ONE)
                : zoomFactor.invert().subtract(Fraction.ONE).invert();
        if (newZoom.doubleValue() > ZOOM_MAXIMUM) {
            newZoom = Fraction.getFraction(ZOOM_MAXIMUM);
        }
        setScaleX(newZoom.doubleValue());
        setScaleY(newZoom.doubleValue());
    }

    public void zoomOut() {
        Fraction zoomFactor = Fraction.getFraction(getScaleX());
        Fraction newZoom = zoomFactor.doubleValue() > 1
                ? zoomFactor.subtract(Fraction.ONE)
                : zoomFactor.invert().add(Fraction.ONE).invert();
        if (newZoom.doubleValue() < ZOOM_MINIMUM) {
            newZoom = Fraction.getFraction(ZOOM_MINIMUM);
        }
        setScaleX(newZoom.doubleValue());
        setScaleY(newZoom.doubleValue());
    }

}
