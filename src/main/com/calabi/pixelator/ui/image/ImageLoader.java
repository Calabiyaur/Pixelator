package com.calabi.pixelator.ui.image;

import com.sun.javafx.tk.PlatformImage;
import com.sun.prism.Image;

class ImageLoader implements com.sun.javafx.tk.ImageLoader {

    private final PlatformImage frameOne;
    private final int frameCount;
    private final int frameDelay;
    private final double width;
    private final double height;

    public ImageLoader(PlatformImage frameOne, int frameCount, int frameDelay, double width, double height) {
        this.frameOne = frameOne;
        this.frameCount = frameCount;
        this.frameDelay = frameDelay;
        this.width = width;
        this.height = height;
    }

    @Override
    public Exception getException() {
        return null;
    }

    @Override
    public int getFrameCount() {
        return frameCount;
    }

    @Override
    public PlatformImage getFrame(int index) {
        return index == 0 ? frameOne : blank(width, height);
    }

    @Override
    public int getFrameDelay(int index) {
        return frameDelay;
    }

    @Override
    public int getLoopCount() {
        return 0;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    public static Image blank(double width, double height) {
        return Image.fromIntArgbPreData(new int[(int) (width * height)], (int) width, (int) height);
    }

}
