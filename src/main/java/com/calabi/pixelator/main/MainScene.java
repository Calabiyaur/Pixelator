package com.calabi.pixelator.main;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.sun.javafx.tk.PlatformImage;

import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.config.Theme;
import com.calabi.pixelator.file.FileException;
import com.calabi.pixelator.file.Files;
import com.calabi.pixelator.file.ImageFile;
import com.calabi.pixelator.file.PaletteFile;
import com.calabi.pixelator.file.PixelFile;
import com.calabi.pixelator.project.Project;
import com.calabi.pixelator.ui.control.BasicMenuBar;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.meta.Direction;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolBar;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.dialog.ChangeColorDialog;
import com.calabi.pixelator.view.dialog.ChangePaletteDialog;
import com.calabi.pixelator.view.dialog.ExportStripDialog;
import com.calabi.pixelator.view.dialog.FpsDialog;
import com.calabi.pixelator.view.dialog.MoveImageDialog;
import com.calabi.pixelator.view.dialog.NewImageDialog;
import com.calabi.pixelator.view.dialog.NewProjectDialog;
import com.calabi.pixelator.view.dialog.OpenRecentProjectDialog;
import com.calabi.pixelator.view.dialog.OutlineDialog;
import com.calabi.pixelator.view.dialog.ResizeDialog;
import com.calabi.pixelator.view.dialog.RotateDialog;
import com.calabi.pixelator.view.dialog.SettingsDialog;
import com.calabi.pixelator.view.dialog.StretchDialog;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.editor.ImageEditor;
import com.calabi.pixelator.view.palette.PaletteMaster;
import com.calabi.pixelator.view.palette.PaletteSelection;

import static com.calabi.pixelator.config.Action.*;

public class MainScene extends Scene {

    private static final ObjectProperty<Theme> theme = new SimpleObjectProperty<>();
    private static final ObservableList<String> styleSheets = FXCollections.observableArrayList();

    private final PaletteSelection paletteSelection;

    public MainScene() {
        super(new VBox());
        VBox root = (VBox) getRoot();
        SplitPane splitPane = new SplitPane();
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        double width = Config.SCREEN_WIDTH.getDouble();
        double height = Config.SCREEN_HEIGHT.getDouble();
        root.setPrefSize(width, height);

        registerStyle(getStylesheets());
        setTheme(Theme.valueOf(Config.THEME.getString()));

        createKeyListener();
        IWC.get().setOnKeyPressed(this.getOnKeyPressed());

        VBox barBox = new VBox();
        root.getChildren().add(barBox);

        ToolView toolView = ToolView.get();
        splitPane.getItems().add(toolView);

        BorderPane center = new BorderPane(IWC.get());
        center.setStyle("-fx-background-color: -px_armed");
        splitPane.getItems().add(center);

        ColorView colorView = ColorView.get();
        splitPane.getItems().add(colorView);
        paletteSelection = ColorView.getPaletteSelection();

        InfoView infoView = InfoView.get();
        center.setBottom(infoView);

        MenuBar menuBar = createMenuBar();
        ToolBar toolBar = ToolBar.get();
        barBox.getChildren().addAll(menuBar, toolBar);

        root.getChildren().add(splitPane);
        toolView.setMinWidth(208);
        colorView.setMinWidth(292);
        toolView.setMaxWidth(208);
        colorView.setMaxWidth(292);

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

    public static void setTheme(Theme theme) {
        MainScene.theme.set(theme);
        styleSheets.setAll(
                MainScene.class.getResource("/style/basic-theme.css").toExternalForm(),
                MainScene.class.getResource(theme.getPath()).toExternalForm()
        );
    }

    public static ObjectProperty<Theme> themeProperty() {
        return theme;
    }

    public static void registerStyle(ObservableList<String> style) {
        styleSheets.addListener((ListChangeListener<String>) c -> style.setAll(c.getList()));
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

    public boolean cleanAll() {
        return IWC.get().cleanAll(false);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new BasicMenuBar();
        menuBar.setOnMouseEntered(e -> menuBar.setCursor(Cursor.DEFAULT));

        BasicMenu projectMenu = new BasicMenu("Project");
        projectMenu.addItem(NEW_PROJECT, e -> newProject());
        projectMenu.addItem(OPEN_PROJECT, e -> openProject());
        projectMenu.addItem(OPEN_RECENT_PROJECT, e -> openRecentProject());
        //TODO: projectMenu.addItem(CLOSE_PROJECT);
        projectMenu.addSeparator();
        //TODO: projectMenu.addItem(PROJECT_STRETCH);
        //TODO: projectMenu.addItem(PROJECT_CHANGE_PALETTE);
        //TODO: projectMenu.addItem(PROJECT_OUTLINE);
        projectMenu.addItem(SETTINGS, e -> showSettings());

        BasicMenu fileMenu = new BasicMenu("File");
        fileMenu.addItem(NEW, e -> newImage());
        fileMenu.addItem(OPEN, e -> openImages());
        fileMenu.addItem(SAVE, e -> IWC.get().saveCurrentFile(),
                IWC.get().imageSelectedProperty().and(IWC.get().dirtyProperty()));
        fileMenu.addItem(SAVE_AS, e -> Files.get().create(IWC.get().getCurrentFile()), IWC.get().imageSelectedProperty());
        fileMenu.addItem(SAVE_ALL, e -> IWC.get().saveAll(), IWC.get().overallDirtyProperty());
        fileMenu.addItem(CREATE_FROM_CLIPBOARD, e -> createFromClipboard(), Pixelator.clipboardActiveProperty());
        //TODO: IMPORT_STRIP
        fileMenu.addItem(EXPORT_STRIP, e -> exportStrip(),
                IWC.get().imageSelectedProperty().and(IWC.get().imageAnimatedProperty()));
        fileMenu.addItem(CLOSE, e -> IWC.get().closeCurrent(), IWC.get().imageSelectedProperty());
        fileMenu.addItem(CLOSE_ALL, e -> IWC.get().closeAll(), IWC.get().imageSelectedProperty());

        BasicMenu editMenu = new BasicMenu("Edit");
        editMenu.addItem(UNDO, e -> IWC.get().undo(), IWC.get().undoEnabledProperty());
        editMenu.addItem(REDO, e -> IWC.get().redo(), IWC.get().redoEnabledProperty());
        editMenu.addSeparator();
        editMenu.addItem(CUT, e -> getEditor().cut(), IWC.get().selectionActiveProperty());
        editMenu.addItem(COPY, e -> getEditor().copyImage(), IWC.get().selectionActiveProperty());
        editMenu.addItem(PASTE, e -> getEditor().paste(), IWC.get().imageSelectedProperty());
        editMenu.addItem(DELETE, e -> getEditor().removeSelectionAndRegister(), IWC.get().selectionActiveProperty());
        editMenu.addItem(SELECT_ALL, e -> getEditor().selectAll(), IWC.get().imageSelectedProperty());
        editMenu.addItem(INVERT_SELECTION, e -> getEditor().invertSelection(), IWC.get().imageSelectedProperty());

        BasicMenu viewMenu = new BasicMenu("View");
        CheckMenuItem gridItem = viewMenu.addCheckItem(GRID,
                e -> IWC.get().setShowGrid(!IWC.get().showGridProperty().get()), IWC.get().imageSelectedProperty());
        IWC.get().showGridProperty().addListener((ov, o, n) -> gridItem.setSelected(n));
        CheckMenuItem crossHairItem = viewMenu.addCheckItem(CROSSHAIR,
                e -> IWC.get().setShowCrosshair(!IWC.get().showCrosshairProperty().get()),
                IWC.get().imageSelectedProperty());
        IWC.get().showCrosshairProperty().addListener((ov, o, n) -> crossHairItem.setSelected(n));
        CheckMenuItem backgroundItem = viewMenu.addCheckItem(BACKGROUND,
                e -> IWC.get().setShowBackground(!IWC.get().showBackgroundProperty().get()),
                IWC.get().imageSelectedProperty());
        IWC.get().showBackgroundProperty().addListener((ov, o, n) -> backgroundItem.setSelected(n));
        viewMenu.addItem(ZOOM_IN, e -> IWC.get().zoomIn(), IWC.get().imageSelectedProperty());
        viewMenu.addItem(ZOOM_ZERO, e -> IWC.get().zoomZero(), IWC.get().imageSelectedProperty());
        viewMenu.addItem(ZOOM_OUT, e -> IWC.get().zoomOut(), IWC.get().imageSelectedProperty());

        BasicMenu imageMenu = new BasicMenu("Image"); // Center
        imageMenu.addItem(MOVE_IMAGE, e -> moveAction(), IWC.get().imageSelectedProperty());
        imageMenu.addItem(RESIZE, e -> resizeAction(), IWC.get().imageSelectedProperty());
        imageMenu.addItem(STRETCH, e -> stretchAction(), IWC.get().imageSelectedProperty());
        imageMenu.addItem(CROP, e -> getEditor().crop(), IWC.get().imageSelectedProperty());
        imageMenu.addSeparator();
        imageMenu.addItem(FLIP_HORIZONTALLY, e -> getEditor().flipHorizontally(), IWC.get().imageSelectedProperty());
        imageMenu.addItem(FLIP_VERTICALLY, e -> getEditor().flipVertically(), IWC.get().imageSelectedProperty());
        imageMenu.addItem(ROTATE_CLOCKWISE, e -> getEditor().rotateClockwise(), IWC.get().imageSelectedProperty());
        imageMenu.addItem(ROTATE_COUNTER_CLOCKWISE, e -> getEditor().rotateCounterClockwise(),
                IWC.get().imageSelectedProperty());
        imageMenu.addItem(ROTATE, e -> rotateAction(), IWC.get().imageSelectedProperty());
        imageMenu.addSeparator();
        imageMenu.addItem(CHANGE_COLOR, e -> changeColor(), IWC.get().imageSelectedProperty());
        imageMenu.addItem(INVERT, e -> getEditor().invert(), IWC.get().imageSelectedProperty());
        imageMenu.addItem(INVERT_WITHIN_PALETTE, e -> getEditor().invertWithinPalette(), IWC.get().imageSelectedProperty());

        BasicMenu animationMenu = new BasicMenu("Animation");
        animationMenu.addItem(SET_FPS, e -> setDelay(), IWC.get().imageAnimatedProperty());
        animationMenu.addItem(REVERSE, e -> getEditor().reverse(), IWC.get().imageAnimatedProperty());
        animationMenu.addItem(ADD_FRAME, e -> getEditor().addFrame(), IWC.get().imageSelectedProperty());
        animationMenu.addItem(DUPLICATE_FRAME, e -> getEditor().duplicateFrame(), IWC.get().imageSelectedProperty());
        animationMenu.addItem(REMOVE_FRAME, e -> getEditor().removeFrame(), IWC.get().imageAnimatedProperty());
        animationMenu.addItem(MOVE_FRAME_FORWARD, e -> getEditor().moveFrameForward(), IWC.get().imageAnimatedProperty());
        animationMenu.addItem(MOVE_FRAME_BACKWARD, e -> getEditor().moveFrameBackward(), IWC.get().imageAnimatedProperty());

        BasicMenu paletteMenu = new BasicMenu("Palette");
        paletteMenu.addItem(NEW_PALETTE, e -> paletteSelection.createPalette());
        paletteMenu.addItem(OPEN_PALETTE, e -> paletteSelection.openPalette());
        paletteMenu.addItem(EDIT_PALETTE, e -> paletteSelection.editPalette(), paletteSelection.paletteSelectedProperty());
        paletteMenu.addItem(CHANGE_PALETTE_PREVIEW, e -> paletteSelection.changePreview(),
                paletteSelection.paletteSelectedProperty().and(paletteSelection.defaultPaletteSelectedProperty().not()));
        paletteMenu.addItem(CLOSE_PALETTE, e -> paletteSelection.closeCurrent(),
                paletteSelection.paletteSelectedProperty().and(paletteSelection.defaultPaletteSelectedProperty().not()));
        BasicMenu toolMenu = new BasicMenu("Tools");
        toolMenu.addItem(OUTLINE, e -> outline(), IWC.get().imageSelectedProperty());
        toolMenu.addSeparator();
        toolMenu.addItem(EXTRACT_PALETTE, e -> extractPalette(), IWC.get().imageSelectedProperty());
        toolMenu.addItem(CHANGE_PALETTE, e -> changePalette(), IWC.get().imageSelectedProperty());

        menuBar.getMenus()
                .setAll(projectMenu, fileMenu, editMenu, viewMenu, imageMenu, animationMenu, paletteMenu, toolMenu);
        return menuBar;
    }

    private void createKeyListener() {
        ActionManager.registerAction(ESCAPE, e -> IWC.get().escape());
        ActionManager.registerAction(FIT_WINDOW, e -> IWC.get().fitWindow());
        ActionManager.registerAction(RANDOM_COLOR, e -> ColorView.setColor(ColorUtil.getRandomPleasant()));
        ActionManager.registerAction(RIGHT, e -> move(1, 0));
        ActionManager.registerAction(UP, e -> move(0, -1));
        ActionManager.registerAction(LEFT, e -> move(-1, 0));
        ActionManager.registerAction(DOWN, e -> move(0, 1));
        ActionManager.registerAction(SWITCH_TAB, e -> IWC.get().selectNextWindow());
        ActionManager.registerAction(SWITCH_TAB_BACK, e -> IWC.get().selectPreviousWindow());
    }

    private void newProject() {
        Project project = Project.get();
        Project.setSilently(null);

        NewProjectDialog dialog = new NewProjectDialog();
        dialog.showAndFocus();
        dialog.setOnOk(e -> {
            if (dialog.getProject() == null) {
                Project.setSilently(project);
                return;
            }
            dialog.close();
            if (closeAll()) {
                Project.set(dialog.getProject());
            } else {
                Project.setSilently(project);
            }
        });
        dialog.setOnCancel(e -> {
            Project.setSilently(project);
            dialog.close();
        });
    }

    private void openProject() {
        if (closeAll()) {
            Project project = Files.get().openProject();
            if (project != null) {
                Project.set(project);
            }
        }
    }

    private void openRecentProject() {
        Project project = Project.get();
        Project.setSilently(null);

        OpenRecentProjectDialog dialog = new OpenRecentProjectDialog();
        dialog.showAndFocus();
        dialog.setOnOk(e -> {
            if (dialog.getProject() == null) {
                Project.setSilently(project);
                return;
            }
            dialog.close();
            if (closeAll()) {
                Project.set(dialog.getProject());
            } else {
                Project.setSilently(project);
            }
        });
        dialog.setOnCancel(e -> {
            Project.setSilently(project);
            dialog.close();
        });
    }

    private void move(int right, int down) {
        if (getEditor() != null && !getEditor().getSelectionLayer().isEmpty()) {
            getEditor().moveSelection(right, down);
        } else {
            try {
                Point location = MouseInfo.getPointerInfo().getLocation();
                int scale = Math.max(1, (int) getEditor().getImageView().getScaleX());
                int x = (int) location.getX() + right * scale;
                int y = (int) location.getY() + down * scale;
                new Robot().mouseMove(x, y);
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
        Image image = Clipboard.getSystemClipboard().getImage();
        if (image != null) {
            WritableImage writableImage = new WritableImage(image);
            IWC.get().addImage(new ImageFile(null, writableImage));
            getEditor().setCleanImage(writableImage);
            getEditor().updateDirty();
        }
    }

    private void exportStrip() {
        String name = IWC.get().getCurrentFile().getName();

        ExportStripDialog dialog = new ExportStripDialog();
        dialog.showAndFocus();
        dialog.setOnOk(e -> {
            WritableImage animation = IWC.get().getCurrentImage();

            if (dialog.isIndividualFiles()) {
                for (int i = 0; i < animation.getFrames().length; i++) {
                    PlatformImage platformImage = animation.getFrames()[i];
                    WritableImage frame = new WritableImage((com.sun.prism.Image) platformImage);

                    File file = new File(dialog.getOutput(), name + "_" + i + ".png");
                    if (!file.exists()) {
                        try {
                            if (!file.createNewFile()) {
                                throw new FileException("");
                            }
                        } catch (IOException ex) {
                            throw new FileException(ex);
                        }
                    }
                    PixelFile pixelFile = new ImageFile(file, frame);
                    Files.get().saveFile(pixelFile);
                }
            } else {
                WritableImage image = new WritableImage(
                        (int) animation.getWidth() * dialog.getHFrames(),
                        (int) (animation.getHeight() * Math.ceil((double) animation.getFrameCount() / dialog.getHFrames()))
                );
                PixelWriter writer = image.getPixelWriter();
                for (int i = 0; i < animation.getFrames().length; i++) {
                    for (int x = 0; x < animation.getWidth(); x++) {
                        int nx = (i * (int) animation.getWidth()) % (int) image.getWidth() + x;
                        for (int y = 0; y < animation.getHeight(); y++) {
                            int ny = (i / dialog.getHFrames()) * (int) animation.getHeight() + y;
                            writer.setColor(nx, ny, animation.getPixelReader(i).getColor(x, y));
                        }
                    }
                }
                File file = new File(dialog.getOutput(), name + "_strip" + animation.getFrameCount() + ".png");
                if (!file.exists()) {
                    try {
                        if (!file.createNewFile()) {
                            throw new FileException("");
                        }
                    } catch (IOException ex) {
                        throw new FileException(ex);
                    }
                }
                PixelFile pixelFile = new ImageFile(file, image);
                Files.get().saveFile(pixelFile);
            }
            dialog.close();
        });
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

    private void rotateAction() {
        RotateDialog dialog = new RotateDialog();
        dialog.setOnOk(e -> {
            Integer degrees = dialog.getDegrees();
            if (degrees == null) {
                return;
            }
            getEditor().rotate(degrees);
            dialog.close();
            Config.ROTATE_DEGREES.putInt(degrees);
        });
        dialog.showAndFocus();
    }

    private void changeColor() {
        ChangeColorDialog dialog = new ChangeColorDialog(IWC.get().getCurrentImage());
        dialog.setOnOk(e -> {
            getEditor().updateImage(dialog.getImage());
            dialog.close();
        });
        dialog.showAndFocus();
    }

    private void setDelay() {
        FpsDialog dialog = new FpsDialog(IWC.get().getCurrentImage());
        dialog.setOnOk(e -> {
            getEditor().changeDelay(dialog.getImage().getDelay());
            dialog.close();
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
