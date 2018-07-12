package main.java.start;

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
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import main.java.files.Files;
import main.java.files.ImageFile;
import main.java.files.PaletteFile;
import main.java.files.PixelFile;
import main.java.res.Config;
import main.java.res.Images;
import main.java.standard.Direction;
import main.java.standard.control.ResizableBorderPane;
import main.java.view.ColorView;
import main.java.view.InfoView;
import main.java.view.ToolView;
import main.java.view.dialog.MoveImageDialog;
import main.java.view.dialog.NewImageDialog;
import main.java.view.dialog.OutlineDialog;
import main.java.view.dialog.ResizeDialog;
import main.java.view.dialog.StretchDialog;
import main.java.view.editor.ImageEditor;
import main.java.view.editor.ImageWindowContainer;
import main.java.view.palette.PaletteMaster;
import main.java.view.palette.PaletteSelection;

import static main.java.start.ActionManager.Action.CLOSE;
import static main.java.start.ActionManager.Action.CLOSE_PALETTE;
import static main.java.start.ActionManager.Action.COPY;
import static main.java.start.ActionManager.Action.CREATE_FROM_CLIPBOARD;
import static main.java.start.ActionManager.Action.CROP;
import static main.java.start.ActionManager.Action.CROSSHAIR;
import static main.java.start.ActionManager.Action.CUT;
import static main.java.start.ActionManager.Action.DELETE;
import static main.java.start.ActionManager.Action.EXTRACT_PALETTE;
import static main.java.start.ActionManager.Action.FLIP_HORIZONTALLY;
import static main.java.start.ActionManager.Action.FLIP_VERTICALLY;
import static main.java.start.ActionManager.Action.GRID;
import static main.java.start.ActionManager.Action.MOVE_IMAGE;
import static main.java.start.ActionManager.Action.NEW;
import static main.java.start.ActionManager.Action.NEW_PALETTE;
import static main.java.start.ActionManager.Action.OPEN;
import static main.java.start.ActionManager.Action.OPEN_PALETTE;
import static main.java.start.ActionManager.Action.OUTLINE;
import static main.java.start.ActionManager.Action.PASTE;
import static main.java.start.ActionManager.Action.REDO;
import static main.java.start.ActionManager.Action.REDO_PALETTE;
import static main.java.start.ActionManager.Action.RESIZE;
import static main.java.start.ActionManager.Action.ROTATE_CLOCKWISE;
import static main.java.start.ActionManager.Action.ROTATE_COUNTER_CLOCKWISE;
import static main.java.start.ActionManager.Action.SAVE;
import static main.java.start.ActionManager.Action.SAVE_AS;
import static main.java.start.ActionManager.Action.SAVE_PALETTE;
import static main.java.start.ActionManager.Action.SELECT_ALL;
import static main.java.start.ActionManager.Action.STRETCH;
import static main.java.start.ActionManager.Action.UNDO;
import static main.java.start.ActionManager.Action.UNDO_PALETTE;

public class MainScene extends Scene {

    private static List<String> styleSheets = new ArrayList<>();
    private ImageWindowContainer imageContainer;
    private PaletteSelection paletteSelection;

    public MainScene() {
        super(new ResizableBorderPane());
        ResizableBorderPane root = ((ResizableBorderPane) getRoot());

        double width = Config.getDouble(Config.WIDTH, 600);
        double height = Config.getDouble(Config.HEIGHT, 400);
        root.setPrefSize(width, height);

        root.setStyle("-fx-background-color: #BBBBBB");

        styleSheets.add(getClass().getResource("/style/color.css").toExternalForm());
        styleSheets.add(getClass().getResource("/style/togglebutton.css").toExternalForm());
        styleSheets.add(getClass().getResource("/style/toolbutton.css").toExternalForm());
        styleSheets.add(getClass().getResource("/style/window.css").toExternalForm());
        getStylesheets().addAll(getStyle());

        imageContainer = new ImageWindowContainer();

        createKeyListener();
        imageContainer.setOnKeyPressed(this.getOnKeyPressed());

        BorderPane center = new BorderPane();
        InfoView infoView = InfoView.getInstance();
        center.setCenter(imageContainer);
        center.setBottom(infoView);
        root.setCenter(center);

        VBox barBox = new VBox();
        root.setTop(barBox);

        ToolView toolView = ToolView.getInstance();
        root.setLeft(toolView);

        ColorView colorView = ColorView.getInstance();
        colorView.setPrefWidth(291);
        root.setRight(colorView);
        this.paletteSelection = ColorView.getPaletteSelection();

        MenuBar menuBar = createMenuBar();
        ToolBar toolBar = createToolBar();
        barBox.getChildren().addAll(menuBar, toolBar);

        Platform.runLater(() -> {
            double tvWidth = toolView.getWidth();
            imageContainer.translateXProperty().bind(toolView.widthProperty().multiply(-1).add(tvWidth));
        });

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
        MenuBar menuBar = new MenuBar();
        menuBar.setOnMouseEntered(e -> menuBar.setCursor(Cursor.DEFAULT));

        BasicMenu fileMenu = new BasicMenu("File");
        fileMenu.addItem(NEW, "New...", e -> newImage());
        fileMenu.addItem(OPEN, "Open...", e -> openImages());
        fileMenu.addItem(SAVE, "Save", e -> imageContainer.saveCurrentFile(),
                imageContainer.imageSelectedProperty().and(imageContainer.dirtyProperty()));
        fileMenu.addItem(SAVE_AS, "Save As...", e -> Files.get().create(imageContainer.getCurrentFile()),
                imageContainer.imageSelectedProperty());
        fileMenu.addItem(CREATE_FROM_CLIPBOARD, "Create from clipboard", e -> createFromClipboard());
        fileMenu.addItem(CLOSE, "Close", e -> imageContainer.closeCurrent(), imageContainer.imageSelectedProperty());

        BasicMenu editMenu = new BasicMenu("Edit");
        editMenu.addItem(UNDO, "Undo", e -> imageContainer.undo(), imageContainer.undoEnabledProperty());
        editMenu.addItem(REDO, "Redo", e -> imageContainer.redo(), imageContainer.redoEnabledProperty());
        editMenu.addSeparator();
        editMenu.addItem(CUT, "Cut", e -> getEditor().cut(), imageContainer.selectionActiveProperty());
        editMenu.addItem(COPY, "Copy", e -> getEditor().copyImage(), imageContainer.selectionActiveProperty());
        editMenu.addItem(PASTE, "Paste", e -> getEditor().paste(), imageContainer.imageSelectedProperty());
        editMenu.addItem(DELETE, "Delete", e -> getEditor().removeSelectionAndRegister(),
                imageContainer.selectionActiveProperty());
        editMenu.addItem(SELECT_ALL, "Select All", e -> getEditor().selectAll(),
                imageContainer.imageSelectedProperty());

        BasicMenu viewMenu = new BasicMenu("View");
        CheckMenuItem gridItem = viewMenu.addCheckItem(GRID, "Show Grid",
                e -> imageContainer.setShowGrid(!imageContainer.showGridProperty().get()),
                imageContainer.imageSelectedProperty());
        imageContainer.showGridProperty().addListener((ov, o, n) -> gridItem.setSelected(n));
        CheckMenuItem crossHairItem = viewMenu.addCheckItem(CROSSHAIR, "Show Cross-Hair",
                e -> imageContainer.setShowCrossHair(!imageContainer.showCrossHairProperty().get()),
                imageContainer.imageSelectedProperty());
        imageContainer.showCrossHairProperty().addListener((ov, o, n) -> crossHairItem.setSelected(n));

        BasicMenu imageMenu = new BasicMenu("Image"); // Center
        imageMenu.addItem(MOVE_IMAGE, "Move image", e -> moveAction(), imageContainer.imageSelectedProperty());
        imageMenu.addItem(RESIZE, "Resize", e -> resizeAction(), imageContainer.imageSelectedProperty());
        imageMenu.addItem(STRETCH, "Stretch", e -> stretchAction(), imageContainer.imageSelectedProperty());
        imageMenu.addItem(CROP, "Crop", e -> getEditor().crop(), imageContainer.imageSelectedProperty());
        imageMenu.addSeparator();
        imageMenu.addItem(FLIP_HORIZONTALLY, "Flip horizontally", e -> getEditor().flipHorizontally(),
                imageContainer.imageSelectedProperty());
        imageMenu.addItem(FLIP_VERTICALLY, "Flip vertically", e -> getEditor().flipVertically(),
                imageContainer.imageSelectedProperty());
        imageMenu.addItem(ROTATE_CLOCKWISE, "Rotate clockwise", e -> getEditor().rotateClockwise(),
                imageContainer.imageSelectedProperty());
        imageMenu.addItem(ROTATE_COUNTER_CLOCKWISE, "Rotate counter-clockwise",
                e -> getEditor().rotateCounterClockwise(),
                imageContainer.imageSelectedProperty());

        BasicMenu paletteMenu = new BasicMenu("Palette");
        paletteMenu.addItem(NEW_PALETTE, "New...", e -> paletteSelection.createPalette());
        paletteMenu.addItem(OPEN_PALETTE, "Open...", e -> paletteSelection.openPalette());
        paletteMenu.addItem(SAVE_PALETTE, "Save", e -> paletteSelection.savePalette(), paletteSelection.paletteSelectedProperty());
        paletteMenu.addItem(CLOSE_PALETTE, "Close", e -> paletteSelection.closeCurrent(), paletteSelection.paletteSelectedProperty());
        paletteMenu.addSeparator();
        paletteMenu.addItem(UNDO_PALETTE, "Undo", e -> paletteSelection.undo(), paletteSelection.undoEnabledProperty());
        paletteMenu.addItem(REDO_PALETTE, "Redo", e -> paletteSelection.redo(), paletteSelection.redoEnabledProperty());
        paletteMenu.addSeparator();
        paletteMenu.addItem(EXTRACT_PALETTE, "Extract palette", e -> extractPalette(),
                imageContainer.imageSelectedProperty());

        BasicMenu toolMenu = new BasicMenu("Tools");
        toolMenu.addItem(OUTLINE, "Outline", e -> outline(), imageContainer.imageSelectedProperty());

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

        return toolBar;
    }

    private void createKeyListener() {
        ActionManager.registerAction(ActionManager.Action.ESCAPE, e -> imageContainer.escape());
        ActionManager.registerAction(ActionManager.Action.PLUS, e -> imageContainer.zoomIn());
        ActionManager.registerAction(ActionManager.Action.MINUS, e -> imageContainer.zoomOut());
        ActionManager.registerAction(ActionManager.Action.RIGHT, e -> getEditor().moveSelection(1, 0));
        ActionManager.registerAction(ActionManager.Action.UP, e -> getEditor().moveSelection(0, -1));
        ActionManager.registerAction(ActionManager.Action.LEFT, e -> getEditor().moveSelection(-1, 0));
        ActionManager.registerAction(ActionManager.Action.DOWN, e -> getEditor().moveSelection(0, 1));
        ActionManager.registerAction(ActionManager.Action.SWITCH_TAB, e -> imageContainer.selectNextWindow());
        ActionManager.registerAction(ActionManager.Action.FIT_WINDOW, e -> imageContainer.fitWindow());
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
            imageContainer.addImage(new ImageFile(null, new WritableImage(dialog.getImageWidth(), dialog.getImageHeight())));
        });
    }

    private void createFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasImage()) {
            Image image = clipboard.getImage();
            imageContainer.addImage(new ImageFile(null, image));
        }
    }

    private void openImages() {
        for (ImageFile pair : Files.get().openImages()) {
            imageContainer.addImage(pair);
        }
    }

    private ImageEditor getEditor() {
        return ImageWindowContainer.getEditor();
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
        OutlineDialog dialog = new OutlineDialog(getEditor().getImageView().getImage());
        dialog.setOnOk(e -> {
            getEditor().updateImage(dialog.getImage());
            dialog.close();
        });
        dialog.showAndFocus();
    }

    private void extractPalette() {
        PaletteMaster.extractPalette(imageContainer.getCurrentImage());
    }

}
