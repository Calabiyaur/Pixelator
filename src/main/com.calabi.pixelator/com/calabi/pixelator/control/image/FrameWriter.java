package com.calabi.pixelator.control.image;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import com.sun.javafx.tk.PlatformImage;

public class FrameWriter implements PixelWriter {

    private final WritableImage image;
    private int index = 0;

    public FrameWriter(WritableImage image) {
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
    public void setArgb(int x, int y, int argb) {
        image.getFrames()[index].setArgb(x, y, argb);
        pixelsDirty();
    }

    @Override
    public void setColor(int x, int y, Color c) {
        if (c == null) {
            throw new NullPointerException("Color cannot be null");
        }
        int a = (int) Math.round(c.getOpacity() * 255);
        int r = (int) Math.round(c.getRed()     * 255);
        int g = (int) Math.round(c.getGreen()   * 255);
        int b = (int) Math.round(c.getBlue()    * 255);
        setArgb(x, y, (a << 24) | (r << 16) | (g << 8) | b);
    }

    @Override
    public <T extends Buffer> void setPixels(int x, int y, int w, int h, PixelFormat<T> pixelformat, T buffer,
            int scanlineStride) {

        if (pixelformat == null) {
            throw new NullPointerException("PixelFormat cannot be null");
        }
        if (buffer == null) {
            throw new NullPointerException("Buffer cannot be null");
        }

        PlatformImage pimg = image.getFrames()[index];
        pimg.setPixels(x, y, w, h, pixelformat, buffer, scanlineStride);
        pixelsDirty();
    }

    @Override
    public void setPixels(int x, int y, int w, int h, PixelFormat<ByteBuffer> pixelformat, byte[] buffer, int offset,
            int scanlineStride) {

        if (pixelformat == null) {
            throw new NullPointerException("PixelFormat cannot be null");
        }
        if (buffer == null) {
            throw new NullPointerException("Buffer cannot be null");
        }

        PlatformImage pimg = image.getFrames()[index];
        pimg.setPixels(x, y, w, h, pixelformat, buffer, offset, scanlineStride);
        pixelsDirty();
    }

    @Override
    public void setPixels(int x, int y, int w, int h, PixelFormat<IntBuffer> pixelformat, int[] buffer, int offset,
            int scanlineStride) {

        if (pixelformat == null) {
            throw new NullPointerException("PixelFormat cannot be null");
        }
        if (buffer == null) {
            throw new NullPointerException("Buffer cannot be null");
        }

        PlatformImage pimg = image.getFrames()[index];
        pimg.setPixels(x, y, w, h, pixelformat, buffer, offset, scanlineStride);
        pixelsDirty();
    }

    @Override
    public void setPixels(int writex, int writey, int w, int h, PixelReader reader, int readx, int ready) {
        if (reader == null) {
            throw new NullPointerException("Reader cannot be null");
        }
        PlatformImage pimg = image.getFrames()[index];
        pimg.setPixels(writex, writey, w, h, reader, readx, ready);
        pixelsDirty();
    }

    private void pixelsDirty() {
        //TODO: Do we need this?
        //ReflectionUtil.invokeMethod(image, "pixelsDirty");
    }

}
