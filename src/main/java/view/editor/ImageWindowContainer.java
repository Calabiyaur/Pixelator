package main.java.view.editor;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import main.java.files.ImageFile;
import main.java.files.PixelFile;
import main.java.standard.image.ScalableImageView;
import main.java.view.InfoView;
import main.java.view.ToolView;
import main.java.view.dialog.SaveRequestDialog;

public class ImageWindowContainer extends Pane {

    private static ObjectProperty<ImageWindow> currentWindow;
    private BooleanProperty imageSelected = new SimpleBooleanProperty(false);
    private BooleanProperty showGrid = new SimpleBooleanProperty(false);
    private BooleanProperty showCrossHair = new SimpleBooleanProperty(false);
    private BooleanProperty undoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty redoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty selectionActive = new SimpleBooleanProperty(false);
    private BooleanProperty dirty = new SimpleBooleanProperty(false);

    public ImageWindowContainer() {
        currentWindow = new SimpleObjectProperty<>();
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
                ToolView.setPreview(null, null, null);
                InfoView.setMousePosition(null);
            } else {
                undoEnabled.bind(window.getEditor().undoEnabledProperty());
                redoEnabled.bind(window.getEditor().redoEnabledProperty());
                selectionActive.bind(window.getEditor().selectionActiveProperty());
                showGrid.setValue(window.isShowGrid());
                showCrossHair.setValue(window.isShowCrossHair());
                dirty.bind(window.dirtyProperty());
                updateImage(window);
                window.toFront();
            }
        });
    }

    public static ImageEditor getEditor() {
        return currentWindow.get().getEditor();
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

    public void addImage(ImageFile imageFile) {
        addImage(new ScalableImageView(imageFile.getImage()), imageFile);
    }

    private void addImage(ScalableImageView imageView, ImageFile imageFile) {
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
        if (current != null) {
            window.setTranslateX(current.getTranslateX() + 32);
            window.setTranslateY(current.getTranslateY() + 32);
        } else {
            window.setTranslateX(32 - getTranslateX());
            window.setTranslateY(32 - getTranslateY());
        }
        setCurrentWindow(window);
        window.getImageView().imageProperty().addListener((ov, o, n) -> updateImage(window));
    }

    void setCurrentWindow(ImageWindow window) {
        currentWindow.set(window);
    }

    private void updateImage(ImageWindow window) {
        ToolView.setPreview(window.getImageView().getImage(), getEditor().getToolImage(),
                getEditor().getSelectionImage());
        ToolView.setSize(window.getEditor().getImageWidth(), window.getEditor().getImageHeight());
        ToolView.setZoom(window.getImageView().scaleXProperty().doubleValue());
    }

    public boolean closeAll() {
        boolean dirty = false;

        List<ImageWindow> children = FXCollections.observableArrayList(getChildren()).stream()
                .filter(node -> node instanceof ImageWindow)
                .map(node -> (ImageWindow) node).collect(Collectors.toList());

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

        return true;
    }

    public boolean closeCurrent() {
        return currentWindow.get().closeIfClean();
    }

    public void selectNextWindow() {
        int index = getChildren().indexOf(currentWindow.get());
        int next = (index + 1) % getChildren().size();
        setCurrentWindow((ImageWindow) getChildren().get(next));
    }

    public void setShowGrid(boolean value) {
        showGrid.set(value);
        if (currentWindow.get() != null) {
            currentWindow.get().setShowGrid(value);
        }
    }

    public void setShowCrossHair(boolean value) {
        showCrossHair.set(value);
        if (currentWindow.get() != null) {
            currentWindow.get().setShowCrossHair(value);
        }
    }

    public void undo() {
        currentWindow.get().getEditor().undo();
    }

    public void redo() {
        currentWindow.get().getEditor().redo();
    }

    public void escape() {
        currentWindow.get().getEditor().escape();
    }

    public void zoomIn() {
        currentWindow.get().zoomIn();
    }

    public void zoomOut() {
        currentWindow.get().zoomOut();
    }

    public void fitWindow() {
        currentWindow.get().adjustSize();
    }

    public BooleanProperty imageSelectedProperty() {
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

    public BooleanProperty selectionActiveProperty() {
        return selectionActive;
    }

    public BooleanProperty dirtyProperty() {
        return dirty;
    }
}
