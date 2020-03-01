package com.calabi.pixelator.control.image;

import java.lang.ref.WeakReference;

import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;

import com.sun.javafx.tk.PlatformImage;
import org.apache.commons.lang3.NotImplementedException;

import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.util.ReflectionUtil;

public class WritableImage extends javafx.scene.image.WritableImage {

    private boolean animated;

    private SimpleIntegerProperty index;
    private Timeline timeline;

    private ObjectProperty<Image> current = new SimpleObjectProperty<>();
    private ObservableList<Frame> frames = FXCollections.observableArrayList();

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
            PlatformImage[] animFrames = ReflectionUtil.getField(image, "animFrames");
            index = ReflectionUtil.getField(animation, "frameIndex");
            timeline = ReflectionUtil.getField(animation, "timeline");

            ReflectionUtil.setField(this, "animation", animation);
            ReflectionUtil.setField(this, "animFrames", animFrames);
            ReflectionUtil.setField(animation, "imageRef", new WeakReference<>(this));

            timeline.stop();

            if (animFrames == null || animFrames.length == 0) {
                //TODO
                throw new NotImplementedException("");
            }
            for (PlatformImage animFrame : animFrames) {
                frames.add(new Frame(animFrame));
            }
            current.set(frames.get(0).image);

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

    public void next() {
        if (!isAnimated()) {
            throw new IllegalStateException();
        }
        index.set((index.get() + 1) % frames.size());
    }

    public void previous() {
        if (!isAnimated()) {
            throw new IllegalStateException();
        }
        index.set(Math.floorMod(index.get() - 1, frames.size()));
    }

    public void play() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }

    private static class Frame {
        private PlatformImage platformImage;
        private Image image;

        public Frame(PlatformImage platformImage) {
            this.platformImage = platformImage;
            this.image = ImageUtil.fromPlatformImage(platformImage);
        }
    }

}
