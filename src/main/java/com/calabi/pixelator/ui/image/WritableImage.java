package com.calabi.pixelator.ui.image;

import java.lang.ref.WeakReference;
import java.util.Collections;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.util.Duration;

import com.sun.javafx.tk.PlatformImage;
import org.apache.commons.lang3.NotImplementedException;

import com.calabi.pixelator.file.Category;
import com.calabi.pixelator.file.PixelFile;
import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.Param;
import com.calabi.pixelator.util.ReflectionUtil;

public class WritableImage extends javafx.scene.image.WritableImage {

    public static final int DEFAULT_FRAME_DELAY = 60;

    private PixelFile file;
    private final SimpleBooleanProperty animated = new SimpleBooleanProperty(false);

    private final SimpleIntegerProperty index = new SimpleIntegerProperty();
    private SimpleIntegerProperty _index;
    private Object anim;
    private Timeline timeline;
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private int delay = DEFAULT_FRAME_DELAY;

    private PlatformImage[] frames;
    private final ObservableList<PlatformImage> frameList = FXCollections.observableArrayList();
    private final IntegerProperty frameCount = new SimpleIntegerProperty();

    private FrameReader frameReader = null;
    private FrameWriter frameWriter = null;

    public WritableImage(String path) {
        this(new Image(path));
    }

    public WritableImage(int width, int height) {
        super(width, height);
    }

    public WritableImage(Image image) {
        super((int) image.getWidth(), (int) image.getHeight());

        PixelReader reader = image.getPixelReader();
        setAnimated(reader == null);

        if (isAnimated()) {
            Platform.runLater(() -> {
                Object animation = initAnimationInternal(image);

                ReflectionUtil.setField(this, "animation", animation);
                ReflectionUtil.setField(this, "animFrames", frames);
                ReflectionUtil.setField(animation, "imageRef", new WeakReference<>(this));

                invalidate();
            });

        } else {

            Object platformImage = ReflectionUtil.getField(image, "platformImage");
            ReflectionUtil.setField(this, "platformImage", platformImage);

        }
    }

    public WritableImage(com.sun.prism.Image image) {
        super(image.getWidth(), image.getHeight());

        Object platformImage = ReflectionUtil.getField(this, "platformImage");
        ReflectionUtil.invokeMethod(platformImage, "set", image);
    }

    public void initAnimation(int frameCount, int frameDelay) {
        Check.ensure(!isAnimated());
        Check.ensure(frameCount > 0);

        ObservableValue<PlatformImage> platformImage = ReflectionUtil.getField(this, "platformImage");
        Check.notNull(platformImage.getValue());

        ImageLoader loader = new ImageLoader(platformImage.getValue(), frameCount, frameDelay, getWidth(), getHeight());
        initializeAnimatedImage(loader);

        initAnimationInternal(this);
        setAnimated(true);
    }

    private void initializeAnimatedImage(ImageLoader loader) {
        final int frameCount = loader.getFrameCount();
        frames = new PlatformImage[frameCount];
        ReflectionUtil.setField(this, "animFrames", frames);

        for (int i = 0; i < frameCount; ++i) {
            frames[i] = loader.getFrame(i);
        }

        PlatformImage zeroFrame = loader.getFrame(0);

        double w = loader.getWidth() / zeroFrame.getPixelScale();
        double h = loader.getHeight() / zeroFrame.getPixelScale();
        ReflectionUtil.invokeMethod(this, "setPlatformImageWH", zeroFrame, w, h);

        ReflectionUtil.setField(this, "isAnimated", true);
        anim = ReflectionUtil.instantiate("javafx.scene.image.Image$Animation",
                new Param<>(Image.class, this), new Param<>(com.sun.javafx.tk.ImageLoader.class, loader));
        ReflectionUtil.setField(this, "animation", anim);
        ReflectionUtil.invokeMethod(anim, "start");
    }

    private Object initAnimationInternal(Image image) {
        anim = ReflectionUtil.getField(image, "animation");
        frames = ReflectionUtil.getField(image, "animFrames");
        _index = ReflectionUtil.getField(anim, "frameIndex");
        index.bindBidirectional(_index);
        timeline = ReflectionUtil.getField(anim, "timeline");

        Check.notNull(timeline);
        timeline.stop();

        if (timeline.getKeyFrames().size() >= 2) {
            delay = (int) timeline.getKeyFrames().get(1).getTime().toMillis();
        }

        if (frames == null || frames.length == 0) {
            //TODO
            throw new NotImplementedException("");
        }
        frameCount.setValue(frames.length);
        frameList.setAll(frames);
        frameList.addListener((ListChangeListener<PlatformImage>) c -> {
            setFrames(c.getList().toArray(new PlatformImage[0]));
        });

        return anim;
    }

    /**
     * Force a method call to {@link javafx.scene.image.Image.Animation#updateImage(int)}
     * which refreshes the currently shown frame.
     */
    private void invalidate() {
        ReflectionUtil.invokeMethod(_index, "invalidated");
    }

    public WritableImage copy() {
        PixelReader r = getPixelReader();

        int width = (int) getWidth();
        int height = (int) getHeight();

        WritableImage copy = new WritableImage(width, height);
        PixelWriter writer = copy.getPixelWriter();
        if (isAnimated()) {
            copy.initAnimation(getFrameCount(), delay);
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

    public void setFile(PixelFile file) {
        if (this.file == null) {
            this.file = file;
        } else {
            throw new IllegalStateException();
        }
    }

    public SimpleBooleanProperty animatedProperty() {
        return animated;
    }

    public boolean isAnimated() {
        return animated.get();
    }

    public void setAnimated(boolean animated) {
        if (file != null) {
            file.setCategory(animated ? Category.ANIMATION : Category.IMAGE);
        }

        this.animated.set(animated);

        frameReader = null;
        frameWriter = null;
        //TODO: Clear unused fields (animation, frames, ...)
    }

    public int getIndex() {
        return index.get();
    }

    public SimpleIntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public BooleanProperty playingProperty() {
        return playing;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        if (delay != this.delay) {
            this.delay = delay;
            resetTimeline();
        }
    }

    public PlatformImage[] getFrames() {
        return frames;
    }

    public ObservableList<PlatformImage> getFrameList() {
        return frameList;
    }

    public PixelReader getPixelReader(int index) {
        if (!isAnimated()) {
            throw new IllegalStateException();
        }
        if (frameReader == null) {
            frameReader = new FrameReader(this);
        }
        frameReader.setIndex(index);
        return frameReader;
    }

    public PixelWriter getPixelWriter(int index) {
        if (!isAnimated()) {
            throw new IllegalStateException();
        }
        if (frameWriter == null) {
            frameWriter = new FrameWriter(this);
        }
        frameWriter.setIndex(index);
        return frameWriter;
    }

    public void addFrame(int index) {
        addFrame(index, ImageLoader.blank(getWidth(), getHeight()));
    }

    public void addFrame(int index, PlatformImage frame) {
        if (!isAnimated()) {
            initAnimation(1, delay);
        }

        frameList.add(index, frame);

        next();
    }

    public void copyFrame(int index) {
        PixelReader reader = isAnimated() ? getPixelReader(index - 1) : getPixelReader();
        WritableImage copy = new WritableImage((int) getWidth(), (int) getHeight());
        PixelWriter writer = copy.getPixelWriter();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                writer.setColor(x, y, reader.getColor(x, y));
            }
        }

        ObservableValue<PlatformImage> platformImage = ReflectionUtil.getField(copy, "platformImage");

        addFrame(index, platformImage.getValue());
    }

    public PlatformImage removeFrame(int index) {
        previous(frames.length - 1);

        PlatformImage removed = frameList.remove(index);

        if (isAnimated() && frames.length == 1) {
            setAnimated(false);
        }

        return removed;
    }

    public void moveFrame(int index, int newIndex, boolean setIndex) {
        newIndex = Math.floorMod(newIndex, frames.length);

        Collections.swap(frameList, index, newIndex);

        if (setIndex) {
            setIndex(newIndex);
        }
    }

    private void setFrames(PlatformImage[] frames) {
        boolean frameLengthChanged = frames.length != this.frames.length;

        if (frames != this.frames) {
            this.frames = frames;
            ReflectionUtil.setField(this, "animFrames", frames);
        }

        if (frameLengthChanged) {
            resetTimeline();
            frameCount.set(frames.length);
        }

        invalidate();
    }

    private void resetTimeline() {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
        int duration = 0;
        for (int i = 0; i < frames.length; ++i) {
            keyFrames.add(new KeyFrame(Duration.millis(duration), new KeyValue(index, i, Interpolator.DISCRETE)));
            duration = duration + delay;
        }
        keyFrames.add(new KeyFrame(Duration.millis(duration)));
        ReflectionUtil.setField(anim, "timeline", timeline);
    }

    public int getFrameCount() {
        return frameCount.get();
    }

    public IntegerProperty frameCountProperty() {
        return frameCount;
    }

    public void next() {
        if (!isAnimated()) {
            throw new IllegalStateException();
        }
        if (playing.get()) {
            stop();
        }
        index.set((index.get() + 1) % frames.length);
    }

    public void previous() {
        previous(frames.length);
    }

    private void previous(int length) {
        if (!isAnimated()) {
            throw new IllegalStateException();
        }
        if (playing.get()) {
            stop();
        }
        index.set(Math.floorMod(index.get() - 1, length));
    }

    public void play() {
        if (isAnimated()) {
            timeline.playFrom(timeline.getKeyFrames().get(getIndex()).getTime());
            playing.set(true);
        }
    }

    public boolean stop() {
        if (!isAnimated()) {
            return false;
        }
        boolean wasRunning = Animation.Status.RUNNING.equals(timeline.getStatus());
        timeline.pause();
        if (wasRunning) {
            playing.set(false);
        }
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

        if (isAnimated() != other.isAnimated()) {
            return false;
        }

        if (isAnimated()) {
            if (frames.length != other.frames.length) {
                return false;
            }

            for (int n = 0; n < frames.length; n++) {
                PlatformImage frame = frames[n];
                PlatformImage otherFrame = other.frames[n];
                if (!frameEquals(frame, otherFrame)) {
                    return false;
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

    public boolean frameEquals(PlatformImage frame, PlatformImage otherFrame) {

        int width = (int) this.getWidth();
        int height = (int) this.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (frame.getArgb(i, j) != otherFrame.getArgb(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

}
