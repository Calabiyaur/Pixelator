package com.calabi.pixelator.ui.image;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

import com.sun.javafx.tk.PlatformImage;

public class FrameReader implements PixelReader {

    private final WritableImage image;
    private int index = 0;

    public FrameReader(WritableImage image) {
        this.image = image;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public PixelFormat getPixelFormat() {
        PlatformImage pimg = image.getFrames()[index];
        return pimg.getPlatformPixelFormat();
    }

    @Override
    public int getArgb(int x, int y) {
        PlatformImage pimg = image.getFrames()[index];
        return pimg.getArgb(x, y);
    }

    @Override
    public Color getColor(int x, int y) {
        int argb = getArgb(x, y);
        int a = argb >>> 24;
        int r = (argb >> 16) & 0xff;
        int g = (argb >>  8) & 0xff;
        int b = (argb      ) & 0xff;
        return Color.rgb(r, g, b, a / 255.0);
    }

    @Override
    public <T extends Buffer> void getPixels(int x, int y, int w, int h, WritablePixelFormat<T> pixelformat, T buffer,
            int scanlineStride) {

        PlatformImage pimg = image.getFrames()[index];
        pimg.getPixels(x, y, w, h, pixelformat, buffer, scanlineStride);
    }

    @Override
    public void getPixels(int x, int y, int w, int h, WritablePixelFormat<ByteBuffer> pixelformat, byte[] buffer, int offset,
            int scanlineStride) {

        PlatformImage pimg = image.getFrames()[index];
        pimg.getPixels(x, y, w, h, pixelformat, buffer, offset, scanlineStride);
    }

    @Override
    public void getPixels(int x, int y, int w, int h, WritablePixelFormat<IntBuffer> pixelformat, int[] buffer, int offset,
            int scanlineStride) {

        PlatformImage pimg = image.getFrames()[index];
        pimg.getPixels(x, y, w, h, pixelformat, buffer, offset, scanlineStride);
    }

}
