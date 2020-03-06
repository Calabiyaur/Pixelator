package com.calabi.pixelator.control.image;

import java.lang.ref.WeakReference;

import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;

import com.sun.javafx.tk.PlatformImage;
import org.apache.commons.lang3.NotImplementedException;

import com.calabi.pixelator.util.ReflectionUtil;

public class WritableImage extends javafx.scene.image.WritableImage {

    private boolean animated;

    private SimpleIntegerProperty index;
    private Timeline timeline;

    private PlatformImage[] frames;
    private IntegerProperty frameLength;

    public WritableImage(String path) {
        this(new Image(path));
    }

    public WritableImage(int width, int height) {
        super(width, height);
    }

    public WritableImage(Image image) {
        super((int) image.getWidth(), (int) image.getHeight());

        PixelReader reader = image.getPixelReader();
        animated = reader == null;

        if (animated) {
            Object animation = ReflectionUtil.getField(image, "animation");
            frames = ReflectionUtil.getField(image, "animFrames");
            index = ReflectionUtil.getField(animation, "frameIndex");
            timeline = ReflectionUtil.getField(animation, "timeline");

            ReflectionUtil.setField(this, "animation", animation);
            ReflectionUtil.setField(this, "animFrames", frames);
            ReflectionUtil.setField(animation, "imageRef", new WeakReference<>(this));

            timeline.stop();

            if (frames == null || frames.length == 0) {
                //TODO
                throw new NotImplementedException("");
            }
            frameLength = new SimpleIntegerProperty(frames.length);

            ReflectionUtil.invokeMethod(index, "invalidated");

        } else {

            Object platformImage = ReflectionUtil.getField(image, "platformImage");
            ReflectionUtil.setField(this, "platformImage", platformImage);

        }
    }

    public WritableImage copy() {
        PixelReader r = getPixelReader();

        int width = (int) getWidth();
        int height = (int) getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter writer = writableImage.getPixelWriter();
        writer.setPixels(0, 0, width, height, r, 0, 0);
        return writableImage;
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public int getIndex() {
        return index == null ? 0 : index.get();
    }

    public SimpleIntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public PlatformImage[] getFrames() {
        return frames;
    }

    public int getFrameLength() {
        return frameLength == null ? 1 : frameLength.get();
    }

    public void next() {
        if (!isAnimated()) {
            throw new IllegalStateException();
        }
        index.set((index.get() + 1) % frames.length);
    }

    public void previous() {
        if (!isAnimated()) {
            throw new IllegalStateException();
        }
        index.set(Math.floorMod(index.get() - 1, frames.length));
    }

    public void play() {
        timeline.playFrom(timeline.getKeyFrames().get(getIndex()).getTime());
    }

    public void stop() {
        timeline.pause();
    }

}
