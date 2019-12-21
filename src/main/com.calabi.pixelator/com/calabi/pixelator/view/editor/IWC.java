package com.calabi.pixelator.view.editor;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import com.calabi.pixelator.control.image.ScalableImageView;
import com.calabi.pixelator.files.Category;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.res.ImageConfig;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.dialog.SaveRequestDialog;
import com.calabi.pixelator.view.tool.ToolManager;

public class IWC extends Pane {

    private static IWC instance;
    private static BooleanProperty imageSelected = new SimpleBooleanProperty(false);
    private ObjectProperty<ImageWindow> currentWindow;
    private BooleanProperty showGrid = new SimpleBooleanProperty(false);
    private BooleanProperty showCrossHair = new SimpleBooleanProperty(false);
    private BooleanProperty undoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty redoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty selectionActive = new SimpleBooleanProperty(false);
    private BooleanProperty dirty = new SimpleBooleanProperty(false);
    private BooleanProperty showBackground = new SimpleBooleanProperty(false);

    private IWC() {
        currentWindow = new SimpleObjectProperty<>();
        ToolManager.imageWindowProperty().bind(currentWindow);
        currentWindow.addListener((ov, o, window) -> {
            imageSelected.setValue(window != null);
            if (window == null) {
                undoEnabled.unbind();
                undoEnabled.set(false);
                redoEnabled.unbind();
                redoEnabled.set(false);
                selectionActive.unbind();
                selectionActive.set(false);
                showGrid.setValue(false);
                showCrossHair.setValue(false);
                dirty.unbind();
                dirty.set(false);
                showBackground.unbind();
                showBackground.set(false);
                ToolView.get().setPreview(null, null, null);
                InfoView.setMousePosition(null);
                InfoView.setColorCount(null);
            } else {
                undoEnabled.bind(window.getEditor().undoEnabledProperty());
                redoEnabled.bind(window.getEditor().redoEnabledProperty());
                selectionActive.bind(window.getEditor().selectionActiveProperty());
                showGrid.setValue(window.getEditor().isShowGrid());
                showCrossHair.setValue(window.getEditor().isShowCrossHair());
                dirty.bind(window.getEditor().dirtyProperty());
                showBackground.setValue(window.getEditor().isShowBackground());
                updateImage(window);
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

    public Image getCurrentImage() {
        return currentWindow.get().getImageView().getImage();
    }

    public PixelFile getCurrentFile() {
        return currentWindow.get().getFile();
    }

    public void saveCurrentFile() {
        currentWindow.get().saveAndUndirty();
    }

    public void addImage(PixelFile imageFile) {
        ImageWindow imageWindow = addImage(new ScalableImageView(imageFile.getImage()), imageFile);
        imageWindow.initConfig();
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
        });
        ImageWindow current = currentWindow.get();

        String configXString = window.getConfig(ImageConfig.X);
        String configYString = window.getConfig(ImageConfig.Y);
        if (current != null) {
            window.setTranslateX(configXString == null ? current.getTranslateX() + 32 : Double.valueOf(configXString));
            window.setTranslateY(configYString == null ? current.getTranslateY() + 32 : Double.valueOf(configYString));
        } else {
            window.setTranslateX(configXString == null ? 32 - getTranslateX() : Double.valueOf(configXString));
            window.setTranslateY(configYString == null ? 32 - getTranslateY() : Double.valueOf(configYString));
        }

        setCurrentWindow(window);
        window.adjustSize();
        window.getImageView().imageProperty().addListener((ov, o, n) -> updateImage(window));
        return window;
    }

    void setCurrentWindow(ImageWindow window) {
        currentWindow.set(window);
    }

    private void updateImage(ImageWindow window) {
        if (window.getFile().getCategory() == Category.IMAGE) {
            ToolView.get().setPreview(window.getImageView().getImage(),
                    window.getEditor().getToolImage(),
                    window.getEditor().getSelectionImage());
            ToolView.get().setSize(window.getEditor().getImageWidth(), window.getEditor().getImageHeight());
            ToolView.get().setZoom(window.getImageView().scaleXProperty().doubleValue());
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
                        }
                    });
                    return true;
                case NO:
                    children.forEach(window -> window.close());
                    return true;
                case CANCEL:
                    return false;
            }
        }

        children.forEach(window -> window.close());
        return true;
    }

    public List<ImageWindow> imageWindows() {
        return FXCollections.observableArrayList(getChildren()).stream()
                    .filter(node -> node instanceof ImageWindow)
                    .map(node -> (ImageWindow) node).collect(Collectors.toList());
    }

    public boolean closeCurrent() {
        return currentWindow.get().closeIfClean();
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

    public void setShowCrossHair(boolean value) {
        showCrossHair.set(value);
        if (currentWindow.get() != null) {
            currentWindow.get().getEditor().setShowCrossHair(value);
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

    public void fitWindow() {
        if (currentWindow.get() != null) {
            currentWindow.get().adjustSize();
        }
    }

    public static BooleanProperty imageSelectedProperty() {
        return imageSelected;
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

    public BooleanProperty showCrossHairProperty() {
        return showCrossHair;
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
}