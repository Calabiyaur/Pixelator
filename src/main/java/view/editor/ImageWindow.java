package main.java.view.editor;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import main.java.files.Files;
import main.java.files.ImageFile;
import main.java.res.Images;
import main.java.standard.Point;
import main.java.standard.control.ImageButton;
import main.java.standard.control.basic.BasicScrollPane;
import main.java.standard.control.basic.BasicWindow;
import main.java.standard.image.ScalableImageView;
import main.java.start.ActionManager;
import main.java.start.Main;
import main.java.view.InfoView;
import main.java.view.ToolView;
import main.java.view.dialog.SaveRequestDialog;

public class ImageWindow extends BasicWindow {

    private ImageEditor imageEditor;
    private ImageFile imageFile;
    private BooleanProperty dirty = new SimpleBooleanProperty(false);
    private BooleanProperty showGrid = new SimpleBooleanProperty(false);
    private BooleanProperty showCrossHair = new SimpleBooleanProperty(false);

    public ImageWindow(ScalableImageView imageView, ImageFile imageFile) {
        super(true);
        this.imageFile = imageFile;
        imageView.imageProperty().addListener((ov, o, n) -> this.imageFile.setImage(n));
        imageEditor = new ImageEditor(imageView);
        setText(imageFile.getName());
        setGraphic(imageEditor);
        setPrefSize(400, 400); //TODO: Use preferences from .pix file

        getContent().setOnRawScroll(e -> onScroll(e));
        setOnMouseClicked(e -> mouseClick(e));
        addOnMouseMoved(e -> mouseMoved(e));
        addOnMouseDragged(e -> mouseMoved(e));
        getEditor().mousePositionProperty().addListener((ov, o, n) -> updateMouse(n));
        showGrid.addListener((ov, o, n) -> imageEditor.setShowGrid(n));
        showCrossHair.addListener((ov, o, n) -> imageEditor.setShowCrossHair(n));
        getClose().setOnAction(e -> closeIfClean());
        dirty.bind(imageEditor.dirtyProperty());
        dirty.addListener((ov, o, n) -> updateDirtyText());
        getImageView().scaleXProperty().addListener((ov, o, n) -> ToolView.setZoom(n.doubleValue()));

        ImageButton adjustSize = new ImageButton(Images.RECTANGLE);
        addButton(adjustSize);
        adjustSize.setOnAction(e -> adjustSize());
        ImageButton popup = new ImageButton(Images.POPUP);
        addButton(popup);
        popup.setOnAction(e -> popupAction());
    }

    private void onScroll(ScrollEvent e) {
        // Step 1. Get mouse position
        //Point mousePosition = getEditor().getMousePosition(e.getX(), e.getY());
        // Step 2. Scroll image
        getImageView().scroll(e);
        // Step 3. Translate image to match mouse position //FIXME
        //Platform.runLater(() -> {
        //    Point newMousePosition = getEditor().getMousePosition(e.getX(), e.getY());
        //    double h = (newMousePosition.getX() - mousePosition.getX()) * getImageView().getScaleX();
        //    double v = (newMousePosition.getY() - mousePosition.getY()) * getImageView().getScaleY();
        //    getContent().translateContent(h, v);
        //});
    }

    public void zoomIn() {
        getImageView().zoomIn();
    }

    public void zoomOut() {
        getImageView().zoomOut();
    }

    private void mouseClick(MouseEvent e) {
        if (MouseButton.PRIMARY.equals(e.getButton())
                && e.getClickCount() == 2
                && isDraggableHere(e)) {

            adjustSize();

        } else if (MouseButton.MIDDLE.equals(e.getButton())
                && isDraggableHere(e)) {

            closeIfClean();
        }
    }

    public void adjustSize() {
        double prefWidth = Math.ceil(getImageView().getWidth() * getImageView().getScaleX()
                + BasicWindow.RESIZE_MARGIN * 2);
        double prefHeight = Math.ceil(getImageView().getHeight() * getImageView().getScaleY() + getHeaderHeight()
                + BasicWindow.RESIZE_MARGIN * 2);

        double maxWidth = ((ImageWindowContainer) getParent()).getWidth();
        double maxHeight = ((ImageWindowContainer) getParent()).getHeight();

        double additionalWidth = 0;
        double additionalHeight = 0;
        if (prefWidth > maxWidth) {
            additionalHeight = BasicScrollPane.SCROLL_BAR_HEIGHT;
        }
        if (prefHeight > maxHeight) {
            additionalWidth = BasicScrollPane.SCROLL_BAR_WIDTH;
        }

        setPrefWidth(Math.min(prefWidth + additionalWidth, maxWidth));
        setPrefHeight(Math.min(prefHeight + additionalHeight, maxHeight));

        Platform.runLater(() -> resetPosition(getPrefWidth(), getPrefHeight()));
    }

    private void mouseMoved(MouseEvent e) {
        if (!isContentHere(e)) {
            InfoView.setMousePosition(null);
        }
    }

    private void popupAction() {
        Stage stage = new Stage();
        Bounds bounds = localToScreen(getBoundsInLocal());
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.titleProperty().bind(textProperty());
        stage.getIcons().add(Images.ICON.getImage());
        Main.getStages().add(stage);

        setGraphic(new Pane());
        setVisible(false);
        BasicScrollPane root = new BasicScrollPane(imageEditor);
        stage.setScene(new Scene(root));

        root.setOnRawScroll(e -> onScroll(e));
        root.setOnMouseClicked(e -> mouseClick(e));
        root.setOnMouseMoved(e -> mouseMoved(e));
        root.setOnMouseDragged(e -> mouseMoved(e));
        stage.getScene().setOnKeyPressed(e -> ActionManager.fire(e));

        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(e -> {
            setGraphic(imageEditor);
            setVisible(true);
        });
        stage.show();
    }

    private void updateMouse(Point p) {
        double posX = p.getX();
        double posY = p.getY();
        ToolView.setPreviewPosition(posX, posY);
        InfoView.setMousePosition(new Point((int) posX, (int) posY));
    }

    public boolean closeIfClean() {
        if (dirty.get()) {
            switch(SaveRequestDialog.display()) {
                case OK:
                    saveAndUndirty();
                    break;
                case NO:
                    break;
                case CANCEL:
                    return false;
            }
        }
        close();
        return true;
    }

    public void saveAndUndirty() {
        Files.get().save(getFile());
        undirty();
    }

    public void saveAndClose() {
        saveAndUndirty();
        close();
    }

    public void undirty() {
        getEditor().undirty();
    }

    public ImageEditor getEditor() {
        return imageEditor;
    }

    public ImageFile getFile() {
        return imageFile;
    }

    public void updateDirtyText() {
        setText(getText().replace(" *", "") + (dirty.get() ? " *" : ""));
    }

    public boolean isShowGrid() {
        return showGrid.get();
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid.set(showGrid);
    }

    public boolean isShowCrossHair() {
        return showCrossHair.get();
    }

    public void setShowCrossHair(boolean showCrossHair) {
        this.showCrossHair.set(showCrossHair);
    }

    public ScalableImageView getImageView() {
        return (ScalableImageView) imageEditor.getImageView();
    }

    public boolean isDirty() {
        return dirty.get();
    }

    public BooleanProperty dirtyProperty() {
        return dirty;
    }
}
