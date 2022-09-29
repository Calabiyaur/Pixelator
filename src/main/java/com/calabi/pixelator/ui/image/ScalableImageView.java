package com.calabi.pixelator.ui.image;

import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;

import org.apache.commons.lang3.math.Fraction;

public class ScalableImageView extends PixelatedImageView {

    private double zoomMinimum = 0.1;
    private double zoomMaximum = 32;

    public ScalableImageView(Image image) {
        this(image, 1.0);
    }

    public ScalableImageView(Image image, double zoom) {
        super(image);
        zoomTo(zoom);
    }

    public void scroll(ScrollEvent e) {
        double deltaY = e.getDeltaY();

        if (!(e.isControlDown() || e.isShiftDown() || e.isAltDown())) {
            if (deltaY > 0) {
                zoomIn();
            } else if (deltaY < 0) {
                zoomOut();
            }
        }
    }

    public void zoomIn() {
        Fraction zoomFactor = Fraction.getFraction(getScaleX());
        Fraction newZoom = zoomFactor.doubleValue() >= 1
                ? zoomFactor.add(Fraction.ONE)
                : zoomFactor.invert().subtract(Fraction.ONE).invert();
        if (newZoom.doubleValue() > zoomMaximum) {
            newZoom = Fraction.getFraction(zoomMaximum);
        }
        zoomTo(newZoom.doubleValue());
    }

    public void zoomOut() {
        Fraction zoomFactor = Fraction.getFraction(getScaleX());
        Fraction newZoom = zoomFactor.doubleValue() > 1
                ? zoomFactor.subtract(Fraction.ONE)
                : zoomFactor.invert().add(Fraction.ONE).invert();
        if (newZoom.doubleValue() < zoomMinimum) {
            newZoom = Fraction.getFraction(zoomMinimum);
        }
        zoomTo(newZoom.doubleValue());
    }

    public void zoomZero() {
        zoomTo(1);
    }

    private void zoomTo(double newZoom) {
        setScaleX(newZoom);
        setScaleY(newZoom);
    }

    public double getZoom() {
        return getScaleX();
    }

    public double getZoomMinimum() {
        return zoomMinimum;
    }

    public void setZoomMinimum(double zoomMinimum) {
        this.zoomMinimum = zoomMinimum;
    }

    public double getZoomMaximum() {
        return zoomMaximum;
    }

    public void setZoomMaximum(double zoomMaximum) {
        this.zoomMaximum = zoomMaximum;
    }

}
