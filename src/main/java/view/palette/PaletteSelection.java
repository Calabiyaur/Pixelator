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

public class PaletteSelection extends BorderPane {

    private final PaletteSelectionModel model;
    private BooleanProperty paletteSelected = new SimpleBooleanProperty(false);

    public PaletteSelection() {
        model = new PaletteSelectionModel();
        PaletteTabButtons tabButtonBox = model.getTabButtons();
        PalettePane palettePane = model.getPalettePane();

        Label title = new Label("PALETTE");
        ImageButton create = new ImageButton(Images.NEW);
        ImageButton open = new ImageButton(Images.OPEN);
        create.setOnAction(e -> createPalette());
        open.setOnAction(e -> openPalette());

        GridPane titlePane = new GridPane();
        ToolBar buttonBox = new ToolBar(create, open);
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

        BooleanBinding paletteVisible = tabButtonBox.sizeProperty().greaterThan(0);
        tabButtonBox.visibleProperty().bind(paletteVisible);
        palettePane.visibleProperty().bind(paletteVisible);

        model.editorProperty().addListener((ov, o, n) -> paletteSelected.set(n != null));
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
        model.getTabButtons().getSelected().setPopupText(file.getName());
    }

    public PaletteEditor getDefaultEditor() {
        return model.getDefaultEditor();
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

    public BooleanProperty paletteSelectedProperty() {
        return paletteSelected;
    }

    public void closeCurrent() {
        model.closeIfClean();
    }

}
