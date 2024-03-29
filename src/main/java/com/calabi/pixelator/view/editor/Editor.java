package com.calabi.pixelator.view.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import com.calabi.pixelator.file.PixelFile;
import com.calabi.pixelator.ui.image.PixelatedImageView;
import com.calabi.pixelator.ui.image.WritableImage;
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

    public abstract void updateImage(WritableImage image);

    public final Image getCleanImage() {
        return cleanImage;
    }

    public final void setCleanImage(Image cleanImage) {
        this.cleanImage = cleanImage;
    }

    public final void undirty() {
        cleanImage = getImage().copy();
        updateDirty();
    }

    public final void updateDirty() {
        dirty.set(!imageView.getImage().equals(cleanImage));
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
