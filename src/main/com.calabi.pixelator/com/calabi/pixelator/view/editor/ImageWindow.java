package com.calabi.pixelator.view.editor;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.control.image.ScalableImageView;
import com.calabi.pixelator.control.parent.BasicScrollPane;
import com.calabi.pixelator.control.parent.BasicWindow;
import com.calabi.pixelator.files.Category;
import com.calabi.pixelator.files.Extension;
import com.calabi.pixelator.files.Files;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.start.ActionManager;
import com.calabi.pixelator.start.Pixelator;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.util.NumberUtil;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.dialog.SaveRequestDialog;
import com.calabi.pixelator.view.palette.PaletteEditor;

public class ImageWindow extends BasicWindow { //TODO: Extract models for image / palette / animation window

    public static final double MIN_WIDTH = 172;
    public static final double MIN_HEIGHT = 112;

    private ImageEditor imageEditor;
    private PixelFile imageFile;

    public ImageWindow(ScalableImageView imageView, PixelFile imageFile) {
        super(true);
        this.imageFile = imageFile;
        imageView.imageProperty().addListener((ov, o, n) -> this.imageFile.setImage(n));
        imageEditor = new ImageEditor(imageFile, imageView);
        setText(imageFile.getName());
        if (imageFile.getCategory() == Category.PALETTE) {
            Image previewImage = ((PaletteFile) imageFile).getPreview();
            setGraphic(previewImage != null ? new ImageView(previewImage) : Images.PALETTE.getImageView());
            if (imageFile.getExtension() != Extension.PALI) {
                imageView.setZoomMinimum(4);
                imageView.setZoomMaximum(24);
                imageView.setScaleX(PaletteEditor.ZOOM_FACTOR);
                imageView.setScaleY(PaletteEditor.ZOOM_FACTOR);
            }
        }
        imageFile.nameProperty().addListener((ov, o, n) -> setText(n));
        setContent(imageEditor);
        switch(imageFile.getCategory()) {
            case ANIMATION:
                ImageButton previousFrame = new ImageButton(Images.ARROW_W);
                previousFrame.setOnAction(e -> imageEditor.previousFrame());
                ImageButton nextFrame = new ImageButton(Images.ARROW_E);
                nextFrame.setOnAction(e -> imageEditor.nextFrame());
                HBox framePane = new HBox(previousFrame, nextFrame);
                setLowerContent(framePane);
                break;
            case IMAGE:
                break;
            case PALETTE:
                Button preview = new Button("Preview");
                preview.setOnAction(e -> ColorView.getPaletteSelection().changePreview(imageFile));
                Button apply = new Button("Apply");
                ColorView.getPaletteSelection().getEditor().updateImage(imageEditor.getImage());
                apply.setDisable(true);

                preview.setGraphic(Images.OPEN.getImageView());
                Region space = new Region();
                HBox.setHgrow(space, Priority.ALWAYS);
                HBox buttons = new HBox(preview, space, apply);
                setLowerContent(buttons);
                break;
        }

        setOnScroll(e -> onScroll(e));
        setOnMouseClicked(e -> mouseClick(e));
        getEditor().mousePositionProperty().addListener((ov, o, n) -> updateMouse(n));
        getEditor().setOnMouseExited(e -> updateMouse(null));
        getClose().setOnAction(e -> closeIfClean());
        getEditor().dirtyProperty().addListener((ov, o, n) -> updateDirtyText());
        getImageView().scaleXProperty().addListener((ov, o, n) -> ToolView.get().setZoom(n.doubleValue()));

        ImageButton adjustSize = new ImageButton(Images.FULL_SCREEN);
        addButton(adjustSize);
        adjustSize.setOnAction(e -> adjustSize());
        ImageButton popup = new ImageButton(Images.POPUP);
        addButton(popup);
        popup.setOnAction(e -> popupAction());

        popup.setDisable(true);
    }

    public void initConfig() {
        getImageView().setScaleX(Config.IMAGE_ZOOM_LEVEL.getDouble(getFile(), getImageView().getScaleX()));
        getImageView().setScaleY(Config.IMAGE_ZOOM_LEVEL.getDouble(getFile(), getImageView().getScaleY()));
        setPrefWidth(Config.IMAGE_WIDTH.getDouble(getFile(), getPrefWidth()));
        setPrefHeight(Config.IMAGE_HEIGHT.getDouble(getFile(), getPrefHeight()));
    }

    private void updateConfig() {
        Config.IMAGE_ZOOM_LEVEL.putDouble(getFile(), imageEditor.getImageView().getScaleX());
        Config.IMAGE_X.putDouble(getFile(), getTranslateX());
        Config.IMAGE_Y.putDouble(getFile(), getTranslateY());
        Config.IMAGE_WIDTH.putDouble(getFile(), getWidth());
        Config.IMAGE_HEIGHT.putDouble(getFile(), getHeight());
        Config.IMAGE_H_SCROLL.putDouble(getFile(), getContent().getHvalue());
        Config.IMAGE_V_SCROLL.putDouble(getFile(), getContent().getVvalue());
    }

    private void onScroll(ScrollEvent e) {
        getImageView().scroll(e);
    }

    public void zoomIn() {
        getImageView().zoomIn();
    }

    public void zoomOut() {
        getImageView().zoomOut();
    }

    public void zoomZero() {
        getImageView().zoomZero();
    }

    private void mouseClick(MouseEvent e) {
        if (MouseButton.PRIMARY.equals(e.getButton()) && e.getClickCount() == 2) {
            adjustSize();
        } else if (MouseButton.MIDDLE.equals(e.getButton()) && e.isStillSincePress()) {
            closeIfClean();
        }
    }

    public void adjustSize() {
        double prefWidth = Math.ceil(getImageView().getWidth() * getImageView().getScaleX()
                + BasicWindow.RESIZE_MARGIN * 2);
        double prefHeight = Math.ceil(getImageView().getHeight() * getImageView().getScaleY()
                + getHeaderHeight()
                + BasicWindow.RESIZE_MARGIN * 2
                + getLowerContentHeight())
                - 1;

        double maxWidth = ((IWC) getParent()).getWidth();
        double maxHeight = ((IWC) getParent()).getHeight();

        double additionalWidth = 0;
        double additionalHeight = 0;
        if (prefWidth > maxWidth) {
            additionalHeight = BasicScrollPane.BAR_BREADTH;
        }
        if (prefHeight > maxHeight) {
            additionalWidth = BasicScrollPane.BAR_BREADTH;
        }

        setPrefWidth(NumberUtil.minMax(MIN_WIDTH, prefWidth + additionalWidth, maxWidth));
        setPrefHeight(NumberUtil.minMax(MIN_HEIGHT, prefHeight + additionalHeight, maxHeight));

        Platform.runLater(() -> {
            resetPosition(getPrefWidth(), getPrefHeight());
            requestLayout();
        });
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
            ToolView.get().setPreviewPosition(posX, posY);
            if (ImageUtil.outOfBounds(getImageView().getImage(), (int) posX, (int) posY)) {
                InfoView.setMousePosition(null);
            } else {
                InfoView.setMousePosition(new Point((int) posX, (int) posY));
            }
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

    public PixelFile getFile() {
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
