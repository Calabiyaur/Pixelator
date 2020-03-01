package com.calabi.pixelator.view.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import com.calabi.pixelator.control.image.PixelatedImageView;
import com.calabi.pixelator.control.image.WritableImage;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.view.undo.UndoManager;
import com.calabi.pixelator.view.undo.Undoable;

public abstract class Editor extends StackPane {

    private final UndoManager undoManager;
    private final PixelatedImageView imageView;
    private final BooleanProperty dirty = new SimpleBooleanProperty(false);
    private final PixelFile pixelFile;
    private Image cleanImage;

    public Editor(PixelFile file, PixelatedImageView imageView) {
        this.pixelFile = file;
        this.imageView = imageView;
        this.undoManager = new UndoManager();
    }

    public final PixelatedImageView getImageView() {
        return imageView;
    }

    public final WritableImage getImage() {
        return (WritableImage) imageView.getImage();
    }

    protected final void register(Undoable change) {
        undoManager.add(change);
        updateDirty();
    }

    public void undo() {
        undoManager.undo();
        updateDirty();
    }

    public void redo() {
        undoManager.redo();
        updateDirty();
    }

    public abstract void updateImage(Image image);

    public final Image getCleanImage() {
        return cleanImage;
    }

    public final void setCleanImage(Image cleanImage) {
        this.cleanImage = cleanImage;
    }

    public final void undirty() {
        cleanImage = new WritableImage(getImage());
        updateDirty();
    }

    public final void updateDirty() {
        dirty.set(!ImageUtil.equals(imageView.getImage(), cleanImage));
    }

    public final BooleanProperty undoEnabledProperty() {
        return undoManager.undoEnabledProperty();
    }

    public final BooleanProperty redoEnabledProperty() {
        return undoManager.redoEnabledProperty();
    }

    public boolean isDirty() {
        return dirty.get();
    }

    public final BooleanProperty dirtyProperty() {
        return dirty;
    }

    public PixelFile getPixelFile() {
        return pixelFile;
    }
}
