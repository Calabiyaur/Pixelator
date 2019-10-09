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
import com.calabi.pixelator.res.ImageConfig;
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

public class ImageWindow extends BasicWindow {

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
            Image previewImage = ((PaletteFile) imageFile).getPreviewImage();
            setGraphic(previewImage != null ? new ImageView(previewImage) : Images.PALETTE.getImageView());
            if (imageFile.getExtension() != Extension.PALI) {
                imageView.setZoomMinimum(4);
                imageView.setZoomMaximum(24);
                imageView.setScaleX(PaletteEditor.ZOOM_FACTOR);
                imageView.setScaleY(PaletteEditor.ZOOM_FACTOR);
            }
        }
        imageFile.nameProperty().addListener((ov, o, n) -> setText(n));
        if (imageFile.getCategory() == Category.IMAGE) {
            setContent(imageEditor);
        } else {
            setContent(imageEditor);
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
        }

        setOnScroll(e -> onScroll(e));
        setOnMouseClicked(e -> mouseClick(e));
        getEditor().mousePositionProperty().addListener((ov, o, n) -> updateMouse(n));
        getEditor().setOnMouseExited(e -> updateMouse(null));
        getClose().setOnAction(e -> closeIfClean());
        getEditor().dirtyProperty().addListener((ov, o, n) -> updateDirtyText());
        getImageView().scaleXProperty().addListener((ov, o, n) -> ToolView.getInstance().setZoom(n.doubleValue()));

        ImageButton adjustSize = new ImageButton(Images.RECTANGLE);
        addButton(adjustSize);
        adjustSize.setOnAction(e -> adjustSize());
        ImageButton popup = new ImageButton(Images.POPUP);
        addButton(popup);
        popup.setOnAction(e -> popupAction());

        popup.setDisable(true);
    }

    public void initConfig() {
        String zoom = getConfig(ImageConfig.ZOOM_LEVEL);
        getImageView().setScaleX(zoom == null ? getImageView().getScaleX() : Double.valueOf(zoom));
        getImageView().setScaleY(zoom == null ? getImageView().getScaleY() : Double.valueOf(zoom));
        String width = getConfig(ImageConfig.WIDTH);
        if (width != null) {
            setPrefWidth(Double.valueOf(width));
        }
        String height = getConfig(ImageConfig.HEIGHT);
        if (height != null) {
            setPrefHeight(Double.valueOf(height));
        }
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
        double prefHeight = Math.ceil(getImageView().getHeight() * getImageView().getScaleY()
                + getHeaderHeight()
                + BasicWindow.RESIZE_MARGIN * 2
                + (Category.PALETTE.equals(getFile().getCategory()) ? 24 : 0));

        double maxWidth = ((ImageWindowContainer) getParent()).getWidth();
        double maxHeight = ((ImageWindowContainer) getParent()).getHeight();

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
            ToolView.getInstance().setPreviewPosition(posX, posY);
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
