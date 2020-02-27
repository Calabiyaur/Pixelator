package com.calabi.pixelator.control.image;

import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;

import org.apache.commons.lang3.math.Fraction;

public class ScalableImageView extends PixelatedImageView {

    private double zoomMinimum = 0.1;
    private double zoomMaximum = 32;

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
        if (newZoom.doubleValue() > zoomMaximum) {
            newZoom = Fraction.getFraction(zoomMaximum);
        }
        setScaleX(newZoom.doubleValue());
        setScaleY(newZoom.doubleValue());
    }

    public void zoomOut() {
        Fraction zoomFactor = Fraction.getFraction(getScaleX());
        Fraction newZoom = zoomFactor.doubleValue() > 1
                ? zoomFactor.subtract(Fraction.ONE)
                : zoomFactor.invert().add(Fraction.ONE).invert();
        if (newZoom.doubleValue() < zoomMinimum) {
            newZoom = Fraction.getFraction(zoomMinimum);
        }
        setScaleX(newZoom.doubleValue());
        setScaleY(newZoom.doubleValue());
    }

    public void zoomZero() {
        setScaleX(1);
        setScaleY(1);
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
