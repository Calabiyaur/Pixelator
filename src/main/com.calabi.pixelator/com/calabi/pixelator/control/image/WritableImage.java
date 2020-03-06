package com.calabi.pixelator.control.image;

import java.lang.ref.WeakReference;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;

import com.sun.javafx.tk.PlatformImage;
import org.apache.commons.lang3.NotImplementedException;

import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.ReflectionUtil;

public class WritableImage extends javafx.scene.image.WritableImage {

    public static final int DEFAULT_FRAME_DELAY = 60;

    private boolean animated;

    private SimpleIntegerProperty index;
    private Timeline timeline;

    private PlatformImage[] frames;
    private IntegerProperty frameCount;

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
            Object animation = initAnimationInternal(image);

            ReflectionUtil.setField(this, "animation", animation);
            ReflectionUtil.setField(this, "animFrames", frames);
            ReflectionUtil.setField(animation, "imageRef", new WeakReference<>(this));

            ReflectionUtil.invokeMethod(index, "invalidated");

        } else {

            Object platformImage = ReflectionUtil.getField(image, "platformImage");
            ReflectionUtil.setField(this, "platformImage", platformImage);

        }
    }

    public void initAnimation(int frameCount, int frameDelay) {
        Check.ensure(!animated);
        Check.ensure(frameCount > 0);

        ImageLoader loader = new ImageLoader(frameCount, frameDelay, getWidth(), getHeight());
        ReflectionUtil.invokeMethod(this, "initializeAnimatedImage", loader);

        initAnimationInternal(this);
        animated = true;
    }

    private Object initAnimationInternal(Image image) {
        Object animation = ReflectionUtil.getField(image, "animation");
        frames = ReflectionUtil.getField(image, "animFrames");
        index = ReflectionUtil.getField(animation, "frameIndex");
        timeline = ReflectionUtil.getField(animation, "timeline");

        assert timeline != null;
        timeline.stop();

        if (frames == null || frames.length == 0) {
            //TODO
            throw new NotImplementedException("");
        }
        frameCount = new SimpleIntegerProperty(frames.length);

        return animation;
    }

    public WritableImage copy() {
        PixelReader r = getPixelReader();

        int width = (int) getWidth();
        int height = (int) getHeight();

        WritableImage copy = new WritableImage(width, height);
        PixelWriter writer = copy.getPixelWriter();
        if (animated) {
            copy.initAnimation(getFrameCount(), DEFAULT_FRAME_DELAY);
            for (int n = 0; n < frames.length; n++) {
                PlatformImage frame = frames[n];
                PlatformImage copyFrame = copy.frames[n];
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        copyFrame.setArgb(i, j, frame.getArgb(i, j));
                    }
                }
            }
        } else {
            writer.setPixels(0, 0, width, height, r, 0, 0);
        }
        return copy;
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

    public int getFrameCount() {
        return frameCount == null ? 1 : frameCount.get();
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

    public boolean stop() {
        boolean wasRunning = Animation.Status.RUNNING.equals(timeline.getStatus());
        timeline.pause();
        return wasRunning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WritableImage other = (WritableImage) o;

        int width = (int) this.getWidth();
        int height = (int) this.getHeight();

        if (width != other.getWidth() || height != other.getHeight()) {
            return false;
        }

        if (animated) {
            for (int n = 0; n < frames.length; n++) {
                PlatformImage frame = frames[n];
                PlatformImage otherFrame = other.frames[n];
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        if (frame.getArgb(i, j) != otherFrame.getArgb(i, j)) {
                            return false;
                        }
                    }
                }
            }
        } else {
            PixelReader reader1 = this.getPixelReader();
            PixelReader reader2 = other.getPixelReader();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (!reader1.getColor(i, j).equals(reader2.getColor(i, j))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
