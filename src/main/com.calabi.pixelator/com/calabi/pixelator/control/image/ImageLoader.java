package com.calabi.pixelator.control.image;

import com.sun.javafx.tk.PlatformImage;
import com.sun.prism.Image;

public class ImageLoader implements com.sun.javafx.tk.ImageLoader {

    private final int frameCount;
    private final int frameDelay;
    private final double width;
    private final double height;

    public ImageLoader(int frameCount, int frameDelay, double width, double height) {
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
        return Image.fromIntArgbPreData(new int[(int) (width * height)], (int) width, (int) height);
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

}
