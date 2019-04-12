package main.java.start;

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

import main.java.control.basic.BasicMenuBar;
import main.java.control.parent.ResizableBorderPane;
import main.java.files.Files;
import main.java.files.ImageFile;
import main.java.files.PaletteFile;
import main.java.files.PixelFile;
import main.java.meta.Direction;
import main.java.res.Action;
import main.java.res.Config;
import main.java.res.Images;
import main.java.util.ColorUtil;
import main.java.util.ImageUtil;
import main.java.view.ColorView;
import main.java.view.InfoView;
import main.java.view.ToolView;
import main.java.view.dialog.ChangePaletteDialog;
import main.java.view.dialog.MoveImageDialog;
import main.java.view.dialog.NewImageDialog;
import main.java.view.dialog.OutlineDialog;
import main.java.view.dialog.ResizeDialog;
import main.java.view.dialog.StretchDialog;
import main.java.view.editor.ImageEditor;
import main.java.view.editor.ImageWindowContainer;
import main.java.view.palette.PaletteMaster;
import main.java.view.palette.PaletteSelection;

import static main.java.res.Action.BACKGROUND;
import static main.java.res.Action.CHANGE_PALETTE;
import static main.java.res.Action.CLOSE;
import static main.java.res.Action.CLOSE_PALETTE;
import static main.java.res.Action.COPY;
import static main.java.res.Action.CREATE_FROM_CLIPBOARD;
import static main.java.res.Action.CROP;
import static main.java.res.Action.CROSSHAIR;
import static main.java.res.Action.CUT;
import static main.java.res.Action.DELETE;
import static main.java.res.Action.EXTRACT_PALETTE;
import static main.java.res.Action.FLIP_HORIZONTALLY;
import static main.java.res.Action.FLIP_VERTICALLY;
import static main.java.res.Action.GRID;
import static main.java.res.Action.MOVE_IMAGE;
import static main.java.res.Action.NEW;
import static main.java.res.Action.NEW_PALETTE;
import static main.java.res.Action.OPEN;
import static main.java.res.Action.OPEN_PALETTE;
import static main.java.res.Action.OUTLINE;
import static main.java.res.Action.PASTE;
import static main.java.res.Action.REDO;
import static main.java.res.Action.RESIZE;
import static main.java.res.Action.ROTATE_CLOCKWISE;
import static main.java.res.Action.ROTATE_COUNTER_CLOCKWISE;
import static main.java.res.Action.SAVE;
import static main.java.res.Action.SAVE_AS;
import static main.java.res.Action.SELECT_ALL;
import static main.java.res.Action.STRETCH;
import static main.java.res.Action.UNDO;

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

        double width = Config.getDouble(Config.WIDTH, 600);
        double height = Config.getDouble(Config.HEIGHT, 400);
        root.setPrefSize(width, height);

        root.setStyle("-fx-background-color: #BBBBBB");

        styleSheets.add(getClass().getResource("/style/togglebutton.css").toExternalForm());
        styleSheets.add(getClass().getResource("/style/toolbutton.css").toExternalForm());
        styleSheets.add(getClass().getResource("/style/window.css").toExternalForm());
        styleSheets.add(getClass().getResource("/style/splitpane.css").toExternalForm());
        getStylesheets().addAll(getStyle());

        imageContainer = new ImageWindowContainer();

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

        setOnKeyPressed(e -> ActionManager.fire(e));
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
        fileMenu.addItem(CREATE_FROM_CLIPBOARD, "Create from clipboard", e -> createFromClipboard());
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
        imageMenu.addItem(MOVE_IMAGE, "Move image", e -> moveAction(), ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(RESIZE, "Resize", e -> resizeAction(), ImageWindowContainer.imageSelectedProperty());
        imageMenu.addItem(STRETCH, "Stretch", e -> stretchAction(), ImageWindowContainer.imageSelectedProperty());
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

        BasicMenu paletteMenu = new BasicMenu("Palette");
        paletteMenu.addItem(NEW_PALETTE, "New...", e -> paletteSelection.createPalette());
        paletteMenu.addItem(OPEN_PALETTE, "Open...", e -> paletteSelection.openPalette());
        paletteMenu.addItem(CLOSE_PALETTE, "Close", e -> paletteSelection.closeCurrent(),
                paletteSelection.paletteSelectedProperty());
        BasicMenu toolMenu = new BasicMenu("Tools");
        toolMenu.addItem(OUTLINE, "Outline", e -> outline(), ImageWindowContainer.imageSelectedProperty());
        toolMenu.addSeparator();
        toolMenu.addItem(EXTRACT_PALETTE, "Extract palette", e -> extractPalette(),
                ImageWindowContainer.imageSelectedProperty());
        toolMenu.addItem(CHANGE_PALETTE, "Change palette", e -> changePalette(),
                ImageWindowContainer.imageSelectedProperty());

        menuBar.getMenus().setAll(fileMenu, editMenu, viewMenu, imageMenu, paletteMenu, toolMenu);
        return menuBar;
    }

    private ToolBar createToolBar() {
        BasicToolBar toolBar = new BasicToolBar();
        toolBar.setOnMouseEntered(e -> toolBar.setCursor(Cursor.DEFAULT));

        toolBar.addButton(NEW, Images.NEW);
        toolBar.addButton(OPEN, Images.OPEN);
        toolBar.addButton(SAVE, Images.SAVE);
        toolBar.addButton(UNDO, Images.UNDO);
        toolBar.addButton(REDO, Images.REDO);
        toolBar.addButton(CUT, Images.CUT);
        toolBar.addButton(COPY, Images.COPY);
        toolBar.addButton(PASTE, Images.PASTE);
        ToggleButton grid = toolBar.addToggle(GRID, Images.GRID);
        imageContainer.showGridProperty().addListener((ov, o, n) -> grid.setSelected(n));
        ToggleButton crosshair = toolBar.addToggle(CROSSHAIR, Images.CROSSHAIR);
        imageContainer.showCrossHairProperty().addListener((ov, o, n) -> crosshair.setSelected(n));
        ToggleButton background = toolBar.addToggle(BACKGROUND, Images.BACKGROUND);
        imageContainer.showBackgroundProperty().addListener((ov, o, n) -> background.setSelected(n));

        return toolBar;
    }

    private void createKeyListener() {
        ActionManager.registerAction(Action.DOWN, e -> move(0, 1));
        ActionManager.registerAction(Action.ESCAPE, e -> imageContainer.escape());
        ActionManager.registerAction(Action.FIT_WINDOW, e -> imageContainer.fitWindow());
        ActionManager.registerAction(Action.LEFT, e -> move(-1, 0));
        ActionManager.registerAction(Action.MINUS, e -> imageContainer.zoomOut());
        ActionManager.registerAction(Action.P_DOWN, e -> movePaletteSelection(0, 1));
        ActionManager.registerAction(Action.P_LEFT, e -> movePaletteSelection(-1, 0));
        ActionManager.registerAction(Action.P_RIGHT, e -> movePaletteSelection(1, 0));
        ActionManager.registerAction(Action.P_UP, e -> movePaletteSelection(0, -1));
        ActionManager.registerAction(Action.RANDOM_COLOR, e -> ColorView.setColor(ColorUtil.getRandomPleasant()));
        ActionManager.registerAction(Action.PLUS, e -> imageContainer.zoomIn());
        ActionManager.registerAction(Action.RIGHT, e -> move(1, 0));
        ActionManager.registerAction(Action.SWITCH_TAB, e -> imageContainer.selectNextWindow());
        ActionManager.registerAction(Action.SWITCH_TAB_BACK, e -> imageContainer.selectPreviousWindow());
        ActionManager.registerAction(Action.UP, e -> move(0, -1));
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

    private void movePaletteSelection(int right, int down) {
        if (paletteSelection.getEditor() != null) {
            paletteSelection.getEditor().moveSelection(right, down);
        }
    }

    public void openFiles(Collection<String> files) {
        for (PixelFile pixelFile : Files.get().openByName(files)) {
            if (pixelFile instanceof ImageFile) {
                imageContainer.addImage((ImageFile) pixelFile);
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
            Config.putBoolean(Config.RESIZE_KEEP_RATIO, dialog.isKeepRatio());
            Config.putString(Config.RESIZE_BIAS, dialog.getBias().name());
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
            Config.putBoolean(Config.STRETCH_KEEP_RATIO, dialog.isKeepRatio());
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
        ColorView.addPalette(PaletteMaster.extractPalette(imageContainer.getCurrentImage()));
    }

    private void changePalette() {
        PaletteFile paletteFile = Files.get().openSinglePalette();
        if (paletteFile == null) {
            return;
        }

        ChangePaletteDialog dialog = new ChangePaletteDialog(imageContainer.getCurrentImage(), paletteFile.getImage());
        dialog.setOnOk(e -> {
            getEditor().updateImage(dialog.getImage());
            dialog.close();
        });
        dialog.showAndFocus();
    }

}
