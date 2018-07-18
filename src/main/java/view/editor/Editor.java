package main.java.view.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import main.java.control.image.PixelatedImageView;
import main.java.util.ImageUtil;
import main.java.view.undo.UndoManager;
import main.java.view.undo.Undoable;

public abstract class Editor extends StackPane {

    private UndoManager undoManager;
    private PixelatedImageView imageView;
    private Image cleanImage;
    private BooleanProperty dirty = new SimpleBooleanProperty(false);

    public Editor(PixelatedImageView imageView) {
        this.imageView = imageView;
        this.undoManager = new UndoManager();
    }

    public final PixelatedImageView getImageView() {
        return imageView;
    }

    public final Image getImage() {
        return imageView.getImage();
    }

    protected final void register(Undoable change) {
        undoManager.add(change);
    }

    public void undo() {
        undoManager.undo();
    }

    public void redo() {
        undoManager.redo();
    }

    public final void setCleanImage(Image cleanImage) {
        this.cleanImage = cleanImage;
    }

    public final void undirty() {
        cleanImage = ImageUtil.createWritableImage(getImage());
        updateDirty();
    }

    protected final void updateDirty() {
        dirty.set(!ImageUtil.equals(imageView.getImage(), cleanImage));
    }

    public final BooleanProperty undoEnabledProperty() {
        return undoManager.undoEnabledProperty();
    }

    public final BooleanProperty redoEnabledProperty() {
        return undoManager.redoEnabledProperty();
    }

    public final BooleanProperty dirtyProperty() {
        return dirty;
    }

}