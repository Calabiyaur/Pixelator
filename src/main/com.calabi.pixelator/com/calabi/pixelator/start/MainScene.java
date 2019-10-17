package com.calabi.pixelator.start;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import com.calabi.pixelator.control.basic.BasicMenuBar;
import com.calabi.pixelator.control.parent.ResizableBorderPane;
import com.calabi.pixelator.files.Files;
import com.calabi.pixelator.files.ImageFile;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.res.Action;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.dialog.ChangePaletteDialog;
import com.calabi.pixelator.view.dialog.MoveImageDialog;
import com.calabi.pixelator.view.dialog.NewImageDialog;
import com.calabi.pixelator.view.dialog.OutlineDialog;
import com.calabi.pixelator.view.dialog.ResizeDialog;
import com.calabi.pixelator.view.dialog.StretchDialog;
import com.calabi.pixelator.view.editor.ImageEditor;
import com.calabi.pixelator.view.editor.ImageWindowContainer;
import com.calabi.pixelator.view.palette.PaletteMaster;
import com.calabi.pixelator.view.palette.PaletteSelection;

import static com.calabi.pixelator.res.Action.BACKGROUND;
import static com.calabi.pixelator.res.Action.CHANGE_PALETTE;
import static com.calabi.pixelator.res.Action.CHANGE_PALETTE_PREVIEW;
import static com.calabi.pixelator.res.Action.CLOSE;
import static com.calabi.pixelator.res.Action.CLOSE_PALETTE;
import static com.calabi.pixelator.res.Action.COPY;
import static com.calabi.pixelator.res.Action.CREATE_FROM_CLIPBOARD;
import static com.calabi.pixelator.res.Action.CROP;
import static com.calabi.pixelator.res.Action.CROSSHAIR;
import static com.calabi.pixelator.res.Action.CUT;
import static com.calabi.pixelator.res.Action.DELETE;
import static com.calabi.pixelator.res.Action.EDIT_PALETTE;
import static com.calabi.pixelator.res.Action.EXTRACT_PALETTE;
import static com.calabi.pixelator.res.Action.FLIP_HORIZONTALLY;
import static com.calabi.pixelator.res.Action.FLIP_VERTICALLY;
import static com.calabi.pixelator.res.Action.GRID;
import static com.calabi.pixelator.res.Action.INVERT;
import static com.calabi.pixelator.res.Action.MOVE_IMAGE;
import static com.calabi.pixelator.res.Action.NEW;
import static com.calabi.pixelator.res.Action.NEW_PALETTE;
import static com.calabi.pixelator.res.Action.OPEN;
import static com.calabi.pixelator.res.Action.OPEN_PALETTE;
import static com.calabi.pixelator.res.Action.OUTLINE;
import static com.calabi.pixelator.res.Action.PASTE;
import static com.calabi.pixelator.res.Action.REDO;
import static com.calabi.pixelator.res.Action.RESIZE;
import static com.calabi.pixelator.res.Action.ROTATE_CLOCKWISE;
import static com.calabi.pixelator.res.Action.ROTATE_COUNTER_CLOCKWISE;
import static com.calabi.pixelator.res.Action.SAVE;
import static com.calabi.pixelator.res.Action.SAVE_AS;
import static com.calabi.pixelator.res.Action.SELECT_ALL;
import static com.calabi.pixelator.res.Action.STRETCH;
import static com.calabi.pixelator.res.Action.UNDO;

public class MainScene extends Scene {

    private static List<String> styleSheets = new ArrayList<>();
    private ImageWindowContainer imageContainer;
    private PaletteSelection paletteSelection;

    public MainScene() {
        super(new ResizableBorderPane());
        ResizableBorderPane root = ((ResizableBorderPane) getRoot());
        Platform.runLater(() -> {
            root.setLeftMargin(0.16359447004608296 * 1304);
            root.setRightMargin(0.2442396313364056 * 1304);
        });

        double width = Config.WIDTH.getDouble();
        double height = Config.HEIGHT.getDouble();
        root.setPrefSize(width, height);

        root.setStyle("-fx-background-color: #BBBBBB");

        styleSheets.add(getClass().getResource("/style/bright-theme.css").toExternalForm());
        getStylesheets().addAll(getStyle());

        imageContainer = ImageWindowContainer.getInstance();

        createKeyListener();
        imageContainer.setOnKeyPressed(this.getOnKeyPressed());

        BorderPane center = new BorderPane(imageContainer);
        center.setStyle("-fx-background-color: #BBBBBB");
        root.setCenter(center);

        VBox barBox = new VBox();
        root.setTop(barBox);

        ToolView toolView = ToolView.getInstance();
        root.setLeft(toolView);

        ColorView colorView = ColorView.getInstance();
        colorView.setPrefWidth(291);
        root.setRight(colorView);
        this.paletteSelection = ColorView.getPaletteSelection();

        InfoView infoView = InfoView.getInstance();
        center.setBottom(infoView);

        MenuBar menuBar = createMenuBar();
        ToolBar toolBar = createToolBar();
        barBox.getChildren().addAll(menuBar, toolBar);

        setOnKeyPressed(e -> {
            ActionManager.fire(e);
            if (getEditor() != null) {
                getEditor().onKeyPressed(e);
            }
        });
        setOnKeyReleased(e -> {
            if (getEditor() != null) {
                getEditor().onKeyReleased(e);
            }
        });
    }

    public static List<String> getStyle() {
        return styleSheets;
    }

    /**
     * True if closing is successful
     */
    public boolean closeAll() {
        return imageContainer.closeAll();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new BasicMenuBar();
        menuBar.setOnMouseEntered(e -> menuBar.setCursor(Cursor.DEFAULT));

        BasicMenu fileMenu = new BasicMenu("File");
        fileMenu.addItem(NEW, "New...", e -> newImage());
        fileMenu.addItem(OPEN, "Open...", e -> openImages());
        fileMenu.addItem(SAVE, "Save", e -> imageContainer.saveCurrentFile(),
                ImageWindowContainer.imageSelectedProperty().and(imageContainer.dirtyProperty()));
        fileMenu.addItem(SAVE_AS, "Save As...", e -> Files.get().create(imageContainer.getCurrentFile()),
                ImageWindowContainer.imageSelectedProperty());
        fileMenu.addItem(CREATE_FROM_CLIPBOARD, "Create from clipboard", e -> createFromClipboard(),
                Pixelator.clipboardActiveProperty());
        fileMenu.addItem(CLOSE, "Close", e -> imageContainer.closeCurrent(),
                ImageWindowContainer.imageSelectedProperty());

        BasicMenu editMenu = new BasicMenu("Edit");
        editMenu.addItem(UNDO, "Undo", e -> imageContainer.undo(), imageContainer.undoEnabledProperty());
        editMenu.addItem(REDO, "Redo", e -> imageContainer.redo(), imageContainer.redoEnabledProperty());
        editMenu.addSeparator();
        editMenu.addItem(CUT, "Cut", e -> getEditor().cut(), imageContainer.selectionActiveProperty());
        editMenu.addItem(COPY, "Copy", e -> getEditor().copyImage(), imageContainer.selectionActiveProperty());
        editMenu.addItem(PASTE, "Paste", e -> getEditor().paste(), ImageWindowContainer.imageSelectedProperty());
        editMenu.addItem(DELETE, "Delete", e -> getEditor().removeSelectionAndRegister(),
                imageContainer.selectionActiveProperty());
        editMenu.addItem(SELECT_ALL, "Select All", e -> getEditor().selectAll(),
                ImageWindowContainer.imageSelectedProperty());

        BasicMenu viewMenu = new BasicMenu("View");
        CheckMenuItem gridItem = viewMenu.addCheckItem(GRID, "Show Grid",
                e -> imageContainer.setShowGrid(!imageContainer.showGridProperty().get()),
                ImageWindowContainer.imageSelectedProperty());
        imageContainer.showGridProperty().addListener((ov, o, n) -> gridItem.setSelected(n));
        CheckMenuItem crossHairItem = viewMenu.addCheckItem(CROSSHAIR, "Show Cross-Hair",
                e -> imageContainer.setShowCrossHair(!imageContainer.showCrossHairProperty().get()),
                ImageWindowContainer.imageSelectedProperty());
        imageContainer.showCrossHairProperty().addListener((ov, o, n) -> crossHairItem.setSelected(n));
        CheckMenuItem backgroundItem = viewMenu.addCheckItem(BACKGROUND, "Show Background",
                e -> imageContainer.setShowBackground(!imageContainer.showBackgroundProperty().get()),
                ImageWindowContainer.imageSelectedProperty());
        imageContainer.showBackgroundProperty().addListener((ov, o, n) -> backgroundItem.setSelected(n));

        BasicMenu imageMenu = new BasicMenu("Image"); // Center
        imageMenu.addItem(MOVE_IMAGE, "Move Image...", e -> moveAction(), ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(RESIZE, "Resize...", e -> resizeAction(), ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(STRETCH, "Stretch...", e -> stretchAction(), ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(CROP, "Crop", e -> getEditor().crop(), ImageWindowContainer.imageSelectedProperty());
        imageMenu.addSeparator();
        imageMenu.addItem(FLIP_HORIZONTALLY, "Flip horizontally", e -> getEditor().flipHorizontally(),
                ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(FLIP_VERTICALLY, "Flip vertically", e -> getEditor().flipVertically(),
                ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(ROTATE_CLOCKWISE, "Rotate clockwise", e -> getEditor().rotateClockwise(),
                ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(ROTATE_COUNTER_CLOCKWISE, "Rotate counter-clockwise",
                e -> getEditor().rotateCounterClockwise(),
                ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(INVERT, "Invert", e -> getEditor().invert(), ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(INVERT, "Invert within Palette", e -> getEditor().invertWithinPalette(),
                ImageWindowContainer.imageSelectedProperty());

        BasicMenu paletteMenu = new BasicMenu("Palette");
        paletteMenu.addItem(NEW_PALETTE, "New...", e -> paletteSelection.createPalette());
        paletteMenu.addItem(OPEN_PALETTE, "Open...", e -> paletteSelection.openPalette());
        paletteMenu.addItem(EDIT_PALETTE, "Edit palette", e -> paletteSelection.editPalette());
        paletteMenu.addItem(CHANGE_PALETTE_PREVIEW, "Change Preview...", e -> paletteSelection.changePreview(),
                paletteSelection.paletteSelectedProperty().and(paletteSelection.defaultPaletteSelectedProperty().not()));
        paletteMenu.addItem(CLOSE_PALETTE, "Close", e -> paletteSelection.closeCurrent(),
                paletteSelection.paletteSelectedProperty().and(paletteSelection.defaultPaletteSelectedProperty().not()));
        BasicMenu toolMenu = new BasicMenu("Tools");
        toolMenu.addItem(OUTLINE, "Outline...", e -> outline(), ImageWindowContainer.imageSelectedProperty());
        toolMenu.addSeparator();
        toolMenu.addItem(EXTRACT_PALETTE, "Extract Palette...", e -> extractPalette(),
                ImageWindowContainer.imageSelectedProperty());
        toolMenu.addItem(CHANGE_PALETTE, "Change Palette...", e -> changePalette(),
                ImageWindowContainer.imageSelectedProperty());

        menuBar.getMenus().setAll(fileMenu, editMenu, viewMenu, imageMenu, paletteMenu, toolMenu);
        return menuBar;
    }

    private ToolBar createToolBar() {
        BasicToolBar toolBar = new BasicToolBar();
        toolBar.setOnMouseEntered(e -> toolBar.setCursor(Cursor.DEFAULT));

        toolBar.addButton(NEW);
        toolBar.addButton(OPEN);
        toolBar.addButton(SAVE);
        toolBar.addButton(UNDO);
        toolBar.addButton(REDO);
        toolBar.addButton(CUT);
        toolBar.addButton(COPY);
        toolBar.addButton(PASTE);
        ToggleButton grid = toolBar.addToggle(GRID, Images.GRID);
        imageContainer.showGridProperty().addListener((ov, o, n) -> grid.setSelected(n));
        ToggleButton crosshair = toolBar.addToggle(CROSSHAIR, Images.CROSSHAIR);
        imageContainer.showCrossHairProperty().addListener((ov, o, n) -> crosshair.setSelected(n));
        ToggleButton background = toolBar.addToggle(BACKGROUND, Images.BACKGROUND);
        imageContainer.showBackgroundProperty().addListener((ov, o, n) -> background.setSelected(n));

        return toolBar;
    }

    private void createKeyListener() {
        ActionManager.registerAction(Action.ESCAPE, e -> imageContainer.escape());
        ActionManager.registerAction(Action.FIT_WINDOW, e -> imageContainer.fitWindow());
        ActionManager.registerAction(Action.MINUS, e -> imageContainer.zoomOut());
        ActionManager.registerAction(Action.RANDOM_COLOR, e -> ColorView.setColor(ColorUtil.getRandomPleasant()));
        ActionManager.registerAction(Action.PLUS, e -> imageContainer.zoomIn());
        ActionManager.registerAction(Action.SWITCH_TAB, e -> imageContainer.selectNextWindow());
        ActionManager.registerAction(Action.SWITCH_TAB_BACK, e -> imageContainer.selectPreviousWindow());
    }

    private void move(int right, int down) {
        if (getEditor() != null && getEditor().selectionActiveProperty().get()) {
            getEditor().moveSelection(right, down);
        } else {
            try {
                Point location = MouseInfo.getPointerInfo().getLocation();
                new Robot().mouseMove((int) location.getX() + right, (int) location.getY() + down);
            } catch (AWTException e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    public void openFiles(Collection<String> files) {
        for (PixelFile pixelFile : Files.get().openByName(files)) {
            if (pixelFile instanceof ImageFile) {
                imageContainer.addImage(pixelFile);
            } else if (pixelFile instanceof PaletteFile) {
                paletteSelection.addPalette((PaletteFile) pixelFile);
            } else {
                throw new IllegalStateException("File should not be of class " + pixelFile.getClass());
            }
        }
    }

    private void newImage() {
        NewImageDialog dialog = new NewImageDialog();
        dialog.showAndFocus();
        dialog.setOnOk(e -> {
            if (dialog.getImageWidth() == null || dialog.getImageHeight() == null) {
                return;
            }
            dialog.close();
            imageContainer
                    .addImage(new ImageFile(null, new WritableImage(dialog.getImageWidth(), dialog.getImageHeight())));
        });
    }

    private void createFromClipboard() {
        Image image = ImageUtil.getFromClipboard();
        if (image != null) {
            imageContainer.addImage(new ImageFile(null, image));
            getEditor().setCleanImage(new WritableImage((int) image.getWidth(), (int) image.getHeight()));
            getEditor().updateDirty();
        }
    }

    private void openImages() {
        for (ImageFile pair : Files.get().openImages()) {
            imageContainer.addImage(pair);
        }
    }

    private ImageEditor getEditor() {
        return imageContainer.getEditor();
    }

    private void moveAction() {
        MoveImageDialog dialog = new MoveImageDialog();
        dialog.setOnOk(e -> {
            if (dialog.getHorizontal() == null || dialog.getVertical() == null) {
                return;
            }
            getEditor().moveImage(dialog.getHorizontal(), dialog.getVertical());
            dialog.close();
        });
        dialog.showAndFocus();
    }

    private void resizeAction() {
        ResizeDialog dialog = new ResizeDialog(getEditor().getImageWidth(), getEditor().getImageHeight());
        dialog.setOnOk(e -> {
            Integer w = dialog.getNewWidth();
            Integer h = dialog.getNewHeight();
            Direction bias = dialog.getBias();
            if (w == null || h == null) {
                return;
            }
            getEditor().resizeCanvas(w, h, bias);
            dialog.close();
            Config.RESIZE_KEEP_RATIO.putBoolean(dialog.isKeepRatio());
            Config.RESIZE_BIAS.putString(dialog.getBias().name());
        });
        dialog.showAndFocus();
    }

    private void stretchAction() {
        StretchDialog dialog = new StretchDialog(getEditor().getImageWidth(), getEditor().getImageHeight());
        dialog.setOnOk(e -> {
            Integer w = dialog.getNewWidth();
            Integer h = dialog.getNewHeight();
            if (w == null || h == null) {
                return;
            }
            getEditor().stretchImage(w, h);
            dialog.close();
            Config.STRETCH_KEEP_RATIO.putBoolean(dialog.isKeepRatio());
        });
        dialog.showAndFocus();
    }

    private void outline() {
        OutlineDialog dialog = new OutlineDialog(imageContainer.getCurrentImage());
        dialog.setOnOk(e -> {
            getEditor().updateImage(dialog.getImage());
            dialog.close();
        });
        dialog.showAndFocus();
    }

    private void extractPalette() {
        ColorView.addPalette(PaletteMaster.extractPalette(imageContainer.getCurrentImage(),
                Config.PALETTE_MAX_COLORS.getInt()));
    }

    private void changePalette() {
        PaletteFile paletteFile = Files.get().openSinglePalette();
        if (paletteFile == null) {
            return;
        }

        ChangePaletteDialog dialog = new ChangePaletteDialog(imageContainer.getCurrentImage(), paletteFile);
        dialog.setOnOk(e -> {
            getEditor().updateImage(dialog.getImage());
            dialog.close();
        });
        dialog.showAndFocus();
    }

}
