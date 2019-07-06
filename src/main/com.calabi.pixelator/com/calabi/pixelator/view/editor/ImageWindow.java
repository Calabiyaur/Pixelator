package com.calabi.pixelator.view.editor;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.sun.javafx.scene.control.Properties;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.control.image.ScalableImageView;
import com.calabi.pixelator.control.parent.BasicScrollPane;
import com.calabi.pixelator.control.parent.BasicWindow;
import com.calabi.pixelator.files.Files;
import com.calabi.pixelator.files.ImageFile;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.res.ImageConfig;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.start.ActionManager;
import com.calabi.pixelator.start.Pixelator;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.dialog.SaveRequestDialog;

public class ImageWindow extends BasicWindow {

    private ImageEditor imageEditor;
    private ImageFile imageFile;

    public ImageWindow(ScalableImageView imageView, ImageFile imageFile) {
        super(true);
        this.imageFile = imageFile;
        imageView.imageProperty().addListener((ov, o, n) -> this.imageFile.setImage(n));
        imageEditor = new ImageEditor(imageFile, imageView);
        setText(imageFile.getName());
        imageFile.nameProperty().addListener((ov, o, n) -> setText(n));
        setContent(imageEditor);
        setPrefSize(400, 400); //TODO: Use preferences from .pix file

        getContent().setOnRawScroll(e -> onScroll(e));
        setOnScroll(e -> onScroll(e));
        setOnMouseClicked(e -> mouseClick(e));
        getEditor().mousePositionProperty().addListener((ov, o, n) -> updateMouse(n));
        getEditor().setOnMouseExited(e -> updateMouse(null));
        getClose().setOnAction(e -> closeIfClean());
        getEditor().dirtyProperty().addListener((ov, o, n) -> updateDirtyText());
        getImageView().scaleXProperty().addListener((ov, o, n) -> ToolView.setZoom(n.doubleValue()));

        ImageButton adjustSize = new ImageButton(Images.RECTANGLE);
        addButton(adjustSize);
        adjustSize.setOnAction(e -> adjustSize());
        ImageButton popup = new ImageButton(Images.POPUP);
        addButton(popup);
        popup.setOnAction(e -> popupAction());
    }

    public void initConfig() {
        String zoom = getConfig(ImageConfig.ZOOM_LEVEL);
        getImageView().setScaleX(zoom == null ? 1 : Double.valueOf(zoom));
        getImageView().setScaleY(zoom == null ? 1 : Double.valueOf(zoom));
        String width = getConfig(ImageConfig.WIDTH);
        setPrefWidth(width == null ? -1 : Double.valueOf(width));
        String height = getConfig(ImageConfig.HEIGHT);
        setPrefHeight(height == null ? -1 : Double.valueOf(height));
    }

    private void updateConfig() {
        putConfig(ImageConfig.ZOOM_LEVEL, String.valueOf(imageEditor.getImageView().getScaleX()));
        putConfig(ImageConfig.X, String.valueOf(getTranslateX()));
        putConfig(ImageConfig.Y, String.valueOf(getTranslateY()));
        putConfig(ImageConfig.WIDTH, String.valueOf(getWidth()));
        putConfig(ImageConfig.HEIGHT, String.valueOf(getHeight()));
        putConfig(ImageConfig.H_SCROLL, String.valueOf(getContent().getHvalue()));
        putConfig(ImageConfig.V_SCROLL, String.valueOf(getContent().getVvalue()));
    }

    public String getConfig(ImageConfig config) {
        return getFile().getProperties().getProperty(config.name());
    }

    private void putConfig(ImageConfig config, String value) {
        getFile().getProperties().put(config.name(), value);
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
        if (MouseButton.PRIMARY.equals(e.getButton()) && e.getClickCount() == 2) {
            adjustSize();
        } else if (MouseButton.MIDDLE.equals(e.getButton())) {
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
            additionalHeight = Properties.DEFAULT_EMBEDDED_SB_BREADTH;
        }
        if (prefHeight > maxHeight) {
            additionalWidth = Properties.DEFAULT_EMBEDDED_SB_BREADTH;
        }

        setPrefWidth(Math.min(prefWidth + additionalWidth, maxWidth));
        setPrefHeight(Math.min(prefHeight + additionalHeight, maxHeight));

        Platform.runLater(() -> resetPosition(getPrefWidth(), getPrefHeight()));
    }

    private void popupAction() {
        Stage stage = new Stage();
        Bounds bounds = localToScreen(getBoundsInLocal());
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.titleProperty().bind(textProperty());
        stage.getIcons().add(Images.ICON.getImage());
        Pixelator.getStages().add(stage);

        setContent(new Pane());
        setVisible(false);
        BasicScrollPane root = new BasicScrollPane(imageEditor);
        stage.setScene(new Scene(root));

        root.setOnMouseClicked(e -> mouseClick(e));
        stage.getScene().setOnKeyPressed(e -> ActionManager.fire(e));

        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(e -> {
            setContent(imageEditor);
            setVisible(true);
        });
        stage.show();
    }

    private void updateMouse(Point p) {
        if (p != null) {
            double posX = p.getX();
            double posY = p.getY();
            ToolView.setPreviewPosition(posX, posY);
            InfoView.setMousePosition(new Point((int) posX, (int) posY));
        } else {
            InfoView.setMousePosition(null);
        }
    }

    public boolean closeIfClean() {
        if (isDirty()) {
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
        updateConfig();
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

    public void close() {
        updateConfig();
        Files.get().saveConfig(getFile());
        super.close();
    }

    public ImageEditor getEditor() {
        return imageEditor;
    }

    public ImageFile getFile() {
        return imageFile;
    }

    public void updateDirtyText() {
        setText(getText().replace(" *", "") + (isDirty() ? " *" : ""));
    }

    public ScalableImageView getImageView() {
        return (ScalableImageView) imageEditor.getImageView();
    }

    public boolean isDirty() {
        return getEditor().isDirty();
    }
}
