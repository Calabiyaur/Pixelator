package com.calabi.pixelator.view.undo;

import com.sun.javafx.tk.PlatformImage;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.calabi.pixelator.control.image.WritableImage;

public abstract class FrameChange implements Undoable {

    private Pair<Integer, PlatformImage> added;
    private Triple<Integer, Integer, PlatformImage> permuted;

    public static FrameChange add(WritableImage image, int index) {
        return new FrameAdd(image, index);
    }

    public static FrameChange remove(WritableImage image, int index) {
        return new FrameRemove(image, index);
    }

    public static FrameChange permute(WritableImage image, int index, int newIndex) {
        return new FramePermute(image, index, newIndex);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public FrameChange copy() {
        return this;
    }

    private static class FrameAdd extends FrameChange {

        private final WritableImage image;
        private final int index;

        public FrameAdd(WritableImage image, int index) {
            this.image = image;
            this.index = index;
            image.addFrame(index);
        }

        @Override
        public void undo() {
            image.removeFrame(index);
        }

        @Override
        public void redo() {
            image.addFrame(index);
        }
    }

    private static class FrameRemove extends FrameChange {

        private final WritableImage image;
        private final int index;
        private final PlatformImage removed;

        public FrameRemove(WritableImage image, int index) {
            this.image = image;
            this.index = index;
            this.removed = image.removeFrame(index);
        }

        @Override
        public void undo() {
            image.addFrame(index, removed);
        }

        @Override
        public void redo() {
            image.removeFrame(index);
        }
    }

    private static class FramePermute extends FrameChange {

        private final WritableImage image;
        private final int index;
        private final int newIndex;

        public FramePermute(WritableImage image, int index, int newIndex) {
            this.image = image;
            this.index = index;
            this.newIndex = newIndex;
            image.moveFrame(index, newIndex);
        }

        @Override
        public void undo() {
            image.moveFrame(newIndex, index);
        }

        @Override
        public void redo() {
            image.moveFrame(index, newIndex);
        }
    }

}
