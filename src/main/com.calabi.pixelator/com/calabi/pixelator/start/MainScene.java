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
import com.calabi.pixelator.res.GridConfig;
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
import com.calabi.pixelator.view.dialog.SettingsDialog;
import com.calabi.pixelator.view.dialog.StretchDialog;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.editor.ImageEditor;
import com.calabi.pixelator.view.palette.PaletteMaster;
import com.calabi.pixelator.view.palette.PaletteSelection;

import static com.calabi.pixelator.res.Action.BACKGROUND;
import static com.calabi.pixelator.res.Action.CHANGE_PALETTE;
import static com.calabi.pixelator.res.Action.CHANGE_PALETTE_PREVIEW;
import static com.calabi.pixelator.res.Action.CLOSE;
import static com.calabi.pixelator.res.Action.CLOSE_ALL;
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
import static com.calabi.pixelator.res.Action.INVERT_SELECTION;
import static com.calabi.pixelator.res.Action.INVERT_WITHIN_PALETTE;
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
import static com.calabi.pixelator.res.Action.SETTINGS;
import static com.calabi.pixelator.res.Action.STRETCH;
import static com.calabi.pixelator.res.Action.UNDO;

public class MainScene extends Scene {

    private static List<String> styleSheets = new ArrayList<>();
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

        createKeyListener();
        IWC.get().setOnKeyPressed(this.getOnKeyPressed());

        BorderPane center = new BorderPane(IWC.get());
        center.setStyle("-fx-background-color: #BBBBBB");
        root.setCenter(center);

        VBox barBox = new VBox();
        root.setTop(barBox);

        ToolView toolView = ToolView.get();
        root.setLeft(toolView);

        ColorView colorView = ColorView.get();
        colorView.setPrefWidth(291);
        root.setRight(colorView);
        paletteSelection = ColorView.getPaletteSelection();

        InfoView infoView = InfoView.get();
        center.setBottom(infoView);

        MenuBar menuBar = createMenuBar();
        ToolBar toolBar = createToolBar();
        barBox.getChildren().addAll(menuBar, toolBar);

        paletteSelection.init();

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
        return IWC.get().closeAll();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new BasicMenuBar();
        menuBar.setOnMouseEntered(e -> menuBar.setCursor(Cursor.DEFAULT));

        BasicMenu fileMenu = new BasicMenu("File");
        fileMenu.addItem(NEW, e -> newImage());
        fileMenu.addItem(OPEN, e -> openImages());
        fileMenu.addItem(SAVE, e -> IWC.get().saveCurrentFile(), IWC.imageSelectedProperty().and(IWC.get().dirtyProperty()));
        fileMenu.addItem(SAVE_AS, e -> Files.get().create(IWC.get().getCurrentFile()), IWC.imageSelectedProperty());
        fileMenu.addItem(CREATE_FROM_CLIPBOARD, e -> createFromClipboard(), Pixelator.clipboardActiveProperty());
        fileMenu.addItem(CLOSE, e -> IWC.get().closeCurrent(), IWC.imageSelectedProperty());
        fileMenu.addItem(CLOSE_ALL, e -> IWC.get().closeAll(), IWC.imageSelectedProperty());
        fileMenu.addItem(SETTINGS, e -> showSettings());

        BasicMenu editMenu = new BasicMenu("Edit");
        editMenu.addItem(UNDO, e -> IWC.get().undo(), IWC.get().undoEnabledProperty());
        editMenu.addItem(REDO, e -> IWC.get().redo(), IWC.get().redoEnabledProperty());
        editMenu.addSeparator();
        editMenu.addItem(CUT, e -> getEditor().cut(), IWC.get().selectionActiveProperty());
        editMenu.addItem(COPY, e -> getEditor().copyImage(), IWC.get().selectionActiveProperty());
        editMenu.addItem(PASTE, e -> getEditor().paste(), IWC.imageSelectedProperty());
        editMenu.addItem(DELETE, e -> getEditor().removeSelectionAndRegister(), IWC.get().selectionActiveProperty());
        editMenu.addItem(SELECT_ALL, e -> getEditor().selectAll(), IWC.imageSelectedProperty());
        editMenu.addItem(INVERT_SELECTION, e -> getEditor().invertSelection(), IWC.imageSelectedProperty());

        BasicMenu viewMenu = new BasicMenu("View");
        CheckMenuItem gridItem = viewMenu.addCheckItem(GRID,
                e -> IWC.get().setShowGrid(!IWC.get().showGridProperty().get()), IWC.imageSelectedProperty());
        IWC.get().showGridProperty().addListener((ov, o, n) -> gridItem.setSelected(n));
        CheckMenuItem crossHairItem = viewMenu.addCheckItem(CROSSHAIR,
                e -> IWC.get().setShowCrossHair(!IWC.get().showCrossHairProperty().get()), IWC.imageSelectedProperty());
        IWC.get().showCrossHairProperty().addListener((ov, o, n) -> crossHairItem.setSelected(n));
        CheckMenuItem backgroundItem = viewMenu.addCheckItem(BACKGROUND,
                e -> IWC.get().setShowBackground(!IWC.get().showBackgroundProperty().get()), IWC.imageSelectedProperty());
        IWC.get().showBackgroundProperty().addListener((ov, o, n) -> backgroundItem.setSelected(n));

        BasicMenu imageMenu = new BasicMenu("Image"); // Center
        imageMenu.addItem(MOVE_IMAGE, e -> moveAction(), IWC.imageSelectedProperty());
        imageMenu.addItem(RESIZE, e -> resizeAction(), IWC.imageSelectedProperty());
        imageMenu.addItem(STRETCH, e -> stretchAction(), IWC.imageSelectedProperty());
        imageMenu.addItem(CROP, e -> getEditor().crop(), IWC.imageSelectedProperty());
        imageMenu.addSeparator();
        imageMenu.addItem(FLIP_HORIZONTALLY, e -> getEditor().flipHorizontally(), IWC.imageSelectedProperty());
        imageMenu.addItem(FLIP_VERTICALLY, e -> getEditor().flipVertically(), IWC.imageSelectedProperty());
        imageMenu.addItem(ROTATE_CLOCKWISE, e -> getEditor().rotateClockwise(), IWC.imageSelectedProperty());
        imageMenu.addItem(ROTATE_COUNTER_CLOCKWISE, e -> getEditor().rotateCounterClockwise(), IWC.imageSelectedProperty());
        imageMenu.addItem(INVERT, e -> getEditor().invert(), IWC.imageSelectedProperty());
        imageMenu.addItem(INVERT_WITHIN_PALETTE, e -> getEditor().invertWithinPalette(), IWC.imageSelectedProperty());

        BasicMenu paletteMenu = new BasicMenu("Palette");
        paletteMenu.addItem(NEW_PALETTE, e -> paletteSelection.createPalette());
        paletteMenu.addItem(OPEN_PALETTE, e -> paletteSelection.openPalette());
        paletteMenu.addItem(EDIT_PALETTE, e -> paletteSelection.editPalette(), paletteSelection.paletteSelectedProperty());
        paletteMenu.addItem(CHANGE_PALETTE_PREVIEW, e -> paletteSelection.changePreview(),
                paletteSelection.paletteSelectedProperty().and(paletteSelection.defaultPaletteSelectedProperty().not()));
        paletteMenu.addItem(CLOSE_PALETTE, e -> paletteSelection.closeCurrent(),
                paletteSelection.paletteSelectedProperty().and(paletteSelection.defaultPaletteSelectedProperty().not()));
        BasicMenu toolMenu = new BasicMenu("Tools");
        toolMenu.addItem(OUTLINE, e -> outline(), IWC.imageSelectedProperty());
        toolMenu.addSeparator();
        toolMenu.addItem(EXTRACT_PALETTE, e -> extractPalette(), IWC.imageSelectedProperty());
        toolMenu.addItem(CHANGE_PALETTE, e -> changePalette(), IWC.imageSelectedProperty());

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
        IWC.get().showGridProperty().addListener((ov, o, n) -> grid.setSelected(n));

        GridConfig gridConfig = Config.GRID_CONFIG.getObject();
        grid.setContextMenu(gridConfig.getContextMenu());
        gridConfig.setOnSelection((xInterval, yInterval) -> {
            if (getEditor() != null) {
                getEditor().setGridInterval(xInterval, yInterval);
            }
        });

        ToggleButton crosshair = toolBar.addToggle(CROSSHAIR, Images.CROSSHAIR);
        IWC.get().showCrossHairProperty().addListener((ov, o, n) -> crosshair.setSelected(n));
        ToggleButton background = toolBar.addToggle(BACKGROUND, Images.BACKGROUND);
        IWC.get().showBackgroundProperty().addListener((ov, o, n) -> background.setSelected(n));

        return toolBar;
    }

    private void createKeyListener() {
        ActionManager.registerAction(Action.ESCAPE, e -> IWC.get().escape());
        ActionManager.registerAction(Action.FIT_WINDOW, e -> IWC.get().fitWindow());
        ActionManager.registerAction(Action.MINUS, e -> IWC.get().zoomOut());
        ActionManager.registerAction(Action.RANDOM_COLOR, e -> ColorView.setColor(ColorUtil.getRandomPleasant()));
        ActionManager.registerAction(Action.PLUS, e -> IWC.get().zoomIn());
        ActionManager.registerAction(Action.SWITCH_TAB, e -> IWC.get().selectNextWindow());
        ActionManager.registerAction(Action.SWITCH_TAB_BACK, e -> IWC.get().selectPreviousWindow());
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
                IWC.get().addImage(pixelFile);
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
            if (dialog.getNewWidth() == null || dialog.getNewHeight() == null) {
                return;
            }
            dialog.close();
            IWC.get().addImage(new ImageFile(null, new WritableImage(dialog.getNewWidth(), dialog.getNewHeight())));
        });
    }

    private void createFromClipboard() {
        Image image = ImageUtil.getFromClipboard();
        if (image != null) {
            IWC.get().addImage(new ImageFile(null, image));
            getEditor().setCleanImage(new WritableImage((int) image.getWidth(), (int) image.getHeight()));
            getEditor().updateDirty();
        }
    }

    private void openImages() {
        for (ImageFile pair : Files.get().openImages()) {
            IWC.get().addImage(pair);
        }
    }

    private void showSettings() {
        SettingsDialog dialog = new SettingsDialog();
        dialog.showAndFocus();
    }

    private ImageEditor getEditor() {
        return IWC.get().getEditor();
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
        OutlineDialog dialog = new OutlineDialog(IWC.get().getCurrentImage());
        dialog.setOnOk(e -> {
            getEditor().updateImage(dialog.getImage());
            dialog.close();
        });
        dialog.showAndFocus();
    }

    private void extractPalette() {
        ColorView.addPalette(PaletteMaster.extractPalette(IWC.get().getCurrentImage(),
                Config.PALETTE_MAX_COLORS.getInt()));
    }

    private void changePalette() {
        PaletteFile paletteFile = Files.get().openSinglePalette();
        if (paletteFile == null) {
            return;
        }

        ChangePaletteDialog dialog = new ChangePaletteDialog(IWC.get().getCurrentImage(), paletteFile);
        dialog.setOnOk(e -> {
            getEditor().updateImage(dialog.getImage());
            dialog.close();
        });
        dialog.showAndFocus();
    }

}
