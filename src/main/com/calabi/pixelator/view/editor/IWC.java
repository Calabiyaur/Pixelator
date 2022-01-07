package com.calabi.pixelator.view.editor;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.layout.Pane;

import com.calabi.pixelator.files.Category;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.meta.CompoundBooleanProperty;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.ui.image.ScalableImageView;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.dialog.SaveRequestDialog;
import com.calabi.pixelator.view.editor.window.ImageWindow;
import com.calabi.pixelator.view.tool.ToolManager;

public class IWC extends Pane {

    private static IWC instance;
    private final BooleanProperty imageSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty imageAnimated = new SimpleBooleanProperty(false);
    private final ObjectProperty<ImageWindow> currentWindow;
    private final BooleanProperty showGrid = new SimpleBooleanProperty(false);
    private final BooleanProperty showCrosshair = new SimpleBooleanProperty(false);
    private final BooleanProperty undoEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty redoEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty selectionActive = new SimpleBooleanProperty(false);
    private final BooleanProperty dirty = new SimpleBooleanProperty(false);
    private final CompoundBooleanProperty overallDirty = new CompoundBooleanProperty(false);
    private final BooleanProperty showBackground = new SimpleBooleanProperty(false);

    ChangeListener<? super Number> frameChangeListener = (ov, o, n)
            -> ToolView.get().setFrameIndex(getCurrentImage().getIndex(), getCurrentImage().getFrameCount());

    private IWC() {
        currentWindow = new SimpleObjectProperty<>();
        ToolManager.imageWindowProperty().bind(currentWindow);
        currentWindow.addListener((ov, o, window) -> {
            imageSelected.setValue(window != null);
            if (window == null) {
                imageAnimated.unbind();
                imageAnimated.set(false);
                undoEnabled.unbind();
                undoEnabled.set(false);
                redoEnabled.unbind();
                redoEnabled.set(false);
                selectionActive.unbind();
                selectionActive.set(false);
                showGrid.setValue(false);
                showCrosshair.setValue(false);
                dirty.unbind();
                dirty.set(false);
                overallDirty.remove(o.getEditor().dirtyProperty());
                showBackground.unbind();
                showBackground.set(false);
                updateImage(o.getImage(), null);
                ToolView.get().setPreview(null, null, null);
                InfoView.setMousePosition(null);
                InfoView.setColorCount(null);
            } else {
                imageAnimated.bind(window.getEditor().imageAnimatedProperty());
                undoEnabled.bind(window.getEditor().undoEnabledProperty());
                redoEnabled.bind(window.getEditor().redoEnabledProperty());
                selectionActive.bind(window.getEditor().selectionActiveProperty());
                showGrid.setValue(window.getEditor().isShowGrid());
                showCrosshair.setValue(window.getEditor().isShowCrosshair());
                dirty.bind(window.getEditor().dirtyProperty());
                overallDirty.add(window.getEditor().dirtyProperty());
                showBackground.setValue(window.getEditor().isShowBackground());
                updateImage(o == null ? null : o.getImage(), window);
                window.toFront();
                window.getEditor().updateColorCount();
            }
        });
    }

    public static IWC get() {
        if (instance == null) {
            instance = new IWC();
        }
        return instance;
    }

    public ImageWindow getCurrentWindow() {
        return currentWindow.get();
    }

    public ImageEditor getEditor() {
        if (currentWindow.get() != null) {
            return currentWindow.get().getEditor();
        } else {
            return null;
        }
    }

    public WritableImage getCurrentImage() {
        return ((WritableImage) currentWindow.get().getImageView().getImage());
    }

    public PixelFile getCurrentFile() {
        return currentWindow.get() == null ? null : currentWindow.get().getFile();
    }

    public void saveCurrentFile() {
        currentWindow.get().saveAndUndirty();
    }

    public void addImage(PixelFile imageFile) {
        ImageWindow imageWindow = addImage(new ScalableImageView(imageFile.getImage()), imageFile);
        if (imageFile.getFile() != null) {
            imageWindow.initConfig();
        }
    }

    private ImageWindow addImage(ScalableImageView imageView, PixelFile imageFile) {
        ImageWindow window = new ImageWindow(imageView, imageFile);
        window.setMinX(0);
        window.setMinY(0);
        window.setMinSize(172, 112);
        window.maxXProperty().bind(widthProperty());
        window.maxYProperty().bind(heightProperty());
        window.setOnKeyPressed(this.getOnKeyPressed());
        getChildren().add(window);
        window.focusedProperty().addListener((ov, o, n) -> {
            if (n) {
                setCurrentWindow(window);
            }
        });
        window.setOnCloseRequest(e -> {
            if (getChildren().size() < 2) {
                setCurrentWindow(null);
            } else {
                selectNextWindow();
            }
            getChildren().remove(window);
            window.dispose();
        });
        ImageWindow current = currentWindow.get();

        double defX = current != null ? current.getTranslateX() : 32 - getTranslateX();
        double defY = current != null ? current.getTranslateY() : 32 - getTranslateY();
        window.setTranslateX(Config.IMAGE_X.getDouble(window.getFile(), defX));
        window.setTranslateY(Config.IMAGE_Y.getDouble(window.getFile(), defY));

        setCurrentWindow(window);
        window.getImageView().imageProperty().addListener((ov, o, n) -> updateImage((WritableImage) o, window));
        return window;
    }

    void setCurrentWindow(ImageWindow window) {
        currentWindow.set(window);
    }

    private void updateImage(WritableImage oldImage, ImageWindow window) {
        if (window != null) {
            if (window.getFile().getCategory() != Category.PALETTE) {
                ToolView.get().setPreview(window.getImage(),
                        window.getEditor().getToolImage(),
                        window.getEditor().getSelectionImage());
                ToolView.get().setSize(window.getEditor().getImageWidth(), window.getEditor().getImageHeight());
                ToolView.get().setZoom(window.getImageView().scaleXProperty().doubleValue());

                if (window.getImage().isAnimated()) {
                    window.getImage().indexProperty().addListener(frameChangeListener);
                    window.getImage().frameCountProperty().addListener(frameChangeListener);
                    frameChangeListener.changed(null, null, null);
                } else {
                    ToolView.get().hideFrameIndex();
                }
            }
        } else {
            ToolView.get().hideFrameIndex();
        }

        if (oldImage != null && oldImage.isAnimated()) {
            oldImage.indexProperty().removeListener(frameChangeListener);
            oldImage.frameCountProperty().removeListener(frameChangeListener);
        }
    }

    public void saveAll() {
        for (ImageWindow window : imageWindows()) {
            if (window.isDirty()) {
                window.saveAndUndirty();
            }
        }
    }

    public boolean closeAll() {
        boolean dirty = false;

        List<ImageWindow> children = imageWindows();

        for (ImageWindow window : children) {
            if (window.isDirty()) {
                dirty = true;
            }
        }

        if (dirty) {
            switch(SaveRequestDialog.display()) {
                case OK:
                    children.forEach(window -> {
                        if (window.isDirty()) {
                            window.saveAndClose();
                        } else {
                            window.close();
                        }
                    });
                    return true;
                case NO:
                    children.forEach(window -> window.close());
                    return true;
                case CANCEL:
                default:
                    return false;
            }
        } else {
            children.forEach(window -> window.close());
            return true;
        }
    }

    public List<ImageWindow> imageWindows() {
        return FXCollections.observableArrayList(getChildren()).stream()
                .filter(node -> node instanceof ImageWindow)
                .map(node -> (ImageWindow) node).collect(Collectors.toList());
    }

    public void closeCurrent() {
        currentWindow.get().closeIfClean();
    }

    public void selectNextWindow() {
        if (getChildren().isEmpty()) {
            return;
        }
        int index = getChildren().indexOf(currentWindow.get());
        int next = (index + 1) % getChildren().size();
        setCurrentWindow((ImageWindow) getChildren().get(next));
    }

    public void selectPreviousWindow() {
        if (getChildren().isEmpty()) {
            return;
        }
        int index = getChildren().indexOf(currentWindow.get());
        int previous = (index - 1) % getChildren().size();
        setCurrentWindow((ImageWindow) getChildren().get(previous));
    }

    public void setShowGrid(boolean value) {
        showGrid.set(value);
        if (currentWindow.get() != null) {
            currentWindow.get().getEditor().setShowGrid(value);
        }
    }

    public void setShowCrosshair(boolean value) {
        showCrosshair.set(value);
        if (currentWindow.get() != null) {
            currentWindow.get().getEditor().setShowCrosshair(value);
        }
    }

    public void setShowBackground(boolean value) {
        showBackground.set(value);
        if (currentWindow.get() != null) {
            currentWindow.get().getEditor().setShowBackground(value);
        }
    }

    public void undo() {
        currentWindow.get().getEditor().undo();
    }

    public void redo() {
        currentWindow.get().getEditor().redo();
    }

    public void escape() {
        if (currentWindow.get() != null) {
            currentWindow.get().getEditor().escape();
        }
    }

    public void zoomIn() {
        if (currentWindow.get() != null) {
            currentWindow.get().zoomIn();
        }
    }

    public void zoomOut() {
        if (currentWindow.get() != null) {
            currentWindow.get().zoomOut();
        }
    }

    public void zoomZero() {
        if (currentWindow.get() != null) {
            currentWindow.get().zoomZero();
        }
    }

    public void fitWindow() {
        if (currentWindow.get() != null) {
            currentWindow.get().adjustSize();
        }
    }

    public BooleanProperty imageSelectedProperty() {
        return imageSelected;
    }

    public BooleanProperty imageAnimatedProperty() {
        return imageAnimated;
    }

    public BooleanProperty undoEnabledProperty() {
        return undoEnabled;
    }

    public BooleanProperty redoEnabledProperty() {
        return redoEnabled;
    }

    public BooleanProperty showGridProperty() {
        return showGrid;
    }

    public BooleanProperty showCrosshairProperty() {
        return showCrosshair;
    }

    public BooleanProperty showBackgroundProperty() {
        return showBackground;
    }

    public BooleanProperty selectionActiveProperty() {
        return selectionActive;
    }

    public BooleanProperty dirtyProperty() {
        return dirty;
    }

    public BooleanProperty overallDirtyProperty() {
        return overallDirty;
    }

}
