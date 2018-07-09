package main.java.view.palette;

import java.io.File;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import main.java.files.Files;
import main.java.files.PaletteFile;
import main.java.res.Images;
import main.java.standard.control.ImageButton;
import main.java.view.ColorView;
import main.java.view.dialog.NewPaletteDialog;

public class PaletteSelection extends BorderPane {

    private PaletteTabPane tabPane;
    private BooleanProperty undoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty redoEnabled = new SimpleBooleanProperty(false);
    private BooleanProperty paletteSelected = new SimpleBooleanProperty(false);

    public PaletteSelection() {
        Label title = new Label("PALETTE");
        tabPane = new PaletteTabPane();
        ImageButton create = new ImageButton(Images.NEW);
        ImageButton open = new ImageButton(Images.OPEN);
        ImageButton save = new ImageButton(Images.SAVE);
        ImageButton undo = new ImageButton(Images.UNDO);
        ImageButton redo = new ImageButton(Images.REDO);
        save.disableProperty().bind(tabPane.getSelectionModel().selectedItemProperty().isNull());
        undo.disableProperty().bind(undoEnabled.not());
        redo.disableProperty().bind(redoEnabled.not());
        create.setOnAction(e -> createPalette());
        open.setOnAction(e -> openPalette());
        save.setOnAction(e -> savePalette());
        undo.setOnAction(e -> undo());
        redo.setOnAction(e -> redo());

        GridPane titlePane = new GridPane();
        HBox buttonBox = new HBox(create, open, save, undo, redo);
        titlePane.add(title, 0, 0);
        titlePane.add(buttonBox, 1, 0);
        GridPane.setHgrow(title, Priority.ALWAYS);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);
        setTop(titlePane);

        tabPane.setOnMouseEntered(e -> setCursor(Cursor.DEFAULT));
        setCenter(tabPane);
        VBox.setVgrow(this, Priority.ALWAYS);

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            paletteSelected.set(n != null);
            if (n == null) {
                undoEnabled.unbind();
                undoEnabled.set(false);
                redoEnabled.unbind();
                redoEnabled.set(false);
            } else {
                PaletteEditor editor = n.getEditor();
                undoEnabled.bind(editor.undoEnabledProperty());
                redoEnabled.bind(editor.redoEnabledProperty());
            }
        });

        ImageButton take = new ImageButton(Images.SUBMIT);
        take.setOnAction(e -> getEditor().setColor(ColorView.getColor()));
        take.disableProperty().bind(paletteSelectedProperty().not());

        VBox vBox = new VBox(take);
        setRight(vBox);
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
        Files.get().save(getFile());
    }

    public void addPalette(PaletteFile file) {
        PaletteEditor paletteEditor = new PaletteEditor(file);
        PaletteTab tab = new PaletteTab(paletteEditor);
        tabPane.addTab(tab, file.isNew() ? "New Palette" : file.getName());
    }

    public Image getPalette() {
        return getEditor().getImage();
    }

    public PaletteFile getFile() {
        return getEditor().getFile();
    }

    public void setFile(File file) {
        PaletteTab tab = tabPane.getSelectionModel().getSelectedItem();
        tab.getEditor().setFile(file);
        tab.setText(file.getName());
    }

    public PaletteEditor getEditor() {
        return tabPane.getSelectionModel().getSelectedItem().getEditor();
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
        tabPane.removeTab(tabPane.getSelectionModel().getSelectedItem());
    }

}
