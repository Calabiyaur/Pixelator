package main.java.view.palette;

import java.io.File;
import java.util.List;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import main.java.control.basic.ImageButton;
import main.java.files.Files;
import main.java.files.PaletteFile;
import main.java.res.Images;
import main.java.view.dialog.NewPaletteDialog;
import main.java.view.editor.ImageWindowContainer;

public class PaletteSelection extends BorderPane {

    private final PaletteSelectionModel model;
    private BooleanProperty undoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty redoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty paletteSelected = new SimpleBooleanProperty(false);
    private BooleanProperty dirty = new SimpleBooleanProperty(false);

    public PaletteSelection() {
        model = new PaletteSelectionModel();
        PaletteTabButtons tabButtonBox = model.getTabButtons();
        PalettePane palettePane = model.getPalettePane();

        Label title = new Label("PALETTE");
        ImageButton create = new ImageButton(Images.NEW);
        ImageButton open = new ImageButton(Images.OPEN);
        ImageButton save = new ImageButton(Images.SAVE);
        ImageButton undo = new ImageButton(Images.UNDO);
        ImageButton redo = new ImageButton(Images.REDO);
        save.disableProperty().bind(dirty.not());
        undo.disableProperty().bind(undoEnabled.not());
        redo.disableProperty().bind(redoEnabled.not());
        create.setOnAction(e -> createPalette());
        open.setOnAction(e -> openPalette());
        save.setOnAction(e -> savePalette());
        undo.setOnAction(e -> undo());
        redo.setOnAction(e -> redo());

        GridPane titlePane = new GridPane();
        ToolBar buttonBox = new ToolBar(create, open, save, undo, redo);
        titlePane.add(title, 0, 0);
        titlePane.add(buttonBox, 1, 0);
        GridPane.setHgrow(title, Priority.ALWAYS);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);
        setTop(titlePane);

        setOnMouseEntered(e -> setCursor(Cursor.DEFAULT));
        setLeft(tabButtonBox);
        BorderPane.setMargin(tabButtonBox, new Insets(0, 6, 0, 0));
        setCenter(palettePane);
        VBox.setVgrow(this, Priority.ALWAYS);

        //ImageButton take = new ImageButton(Images.SUBMIT);
        //take.setOnAction(e -> getEditor().setColor(ColorView.getColor()));
        //take.disableProperty().bind(paletteSelectedProperty().not());

        //ToggleImageButton lock = new ToggleImageButton(Images.LOCK_OPEN, Images.LOCK);
        //lock.selectedProperty().addListener((ov, o, n) -> getEditor().setLocked(n));
        //lock.disableProperty().bind(paletteSelectedProperty().not());

        //VBox vBox = new VBox(take, lock);
        //vBox.setPadding(new Insets(6, 0, 6, 6));
        //vBox.setSpacing(6);
        //setRight(vBox);

        BooleanBinding paletteVisible = tabButtonBox.sizeProperty().greaterThan(1)
                .or(ImageWindowContainer.imageSelectedProperty());
        tabButtonBox.visibleProperty().bind(paletteVisible);
        palettePane.visibleProperty().bind(paletteVisible);

        model.editorProperty().addListener((ov, o, n) -> {
            paletteSelected.set(n != null);
            if (n == null) {
                undoEnabled.unbind();
                undoEnabled.set(false);
                redoEnabled.unbind();
                redoEnabled.set(false);
                dirty.unbind();
                dirty.set(false);
            } else {
                undoEnabled.bind(n.undoEnabledProperty());
                redoEnabled.bind(n.redoEnabledProperty());
                dirty.bind(n.dirtyProperty());
                //lock.setSelected(n.isLocked());
            }
        });
    }

    public void createPalette() {
        NewPaletteDialog dialog = new NewPaletteDialog();
        dialog.showAndFocus();
        dialog.setOnOk(e -> {
            if (dialog.getImageWidth() == null || dialog.getImageHeight() == null) {
                return;
            }
            dialog.close();
            WritableImage image = new WritableImage(dialog.getImageWidth(), dialog.getImageHeight());
            addPalette(new PaletteFile(null, image));
        });
    }

    public void openPalette() {
        List<PaletteFile> palettes = Files.get().openPalettes();
        for (PaletteFile palette : palettes) {
            addPalette(palette);
        }
    }

    public void savePalette() {
        model.saveAndUndirty();
    }

    public void addPalette(PaletteFile file) {
        model.addPalette(file);
        model.initConfig();
    }

    public Image getPalette() {
        return getEditor().getImage();
    }

    public PaletteFile getFile() {
        return getEditor().getFile();
    }

    public void setFile(File file) {
        model.getEditor().setFile(file);
        model.getTabButtons().getSelected().setText(file.getName());
    }

    public PaletteEditor getEditor() {
        return model.getEditor();
    }

    public void undo() {
        getEditor().undo();
    }

    public void redo() {
        getEditor().redo();
    }

    public BooleanProperty undoEnabledProperty() {
        return undoEnabled;
    }

    public BooleanProperty redoEnabledProperty() {
        return redoEnabled;
    }

    public BooleanProperty paletteSelectedProperty() {
        return paletteSelected;
    }

    public void closeCurrent() {
        model.closeIfClean();
    }

}
