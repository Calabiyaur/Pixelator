package com.calabi.pixelator.view.palette;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.res.Images;

final class PaletteSelectionModel {

    private final PaletteTabButtons tabButtons = new PaletteTabButtons();
    private final PalettePane palettePane = new PalettePane();

    private final PaletteEditor defaultEditor;
    private final ObjectProperty<PaletteEditor> paletteEditor = new SimpleObjectProperty<>();

    public PaletteSelectionModel() {
        paletteEditor.addListener((ov, o, n) -> palettePane.setContent(n));
        tabButtons.selectedToggleProperty().addListener((ov, o, n) -> paletteEditor.set(n == null ? null : n.getEditor()));

        WritableImage image = new WritableImage(PaletteEditor.DEFAULT_WIDTH, PaletteEditor.DEFAULT_HEIGHT);
        PaletteFile file = new PaletteFile(null, image);
        defaultEditor = new PaletteEditor(file);
        PaletteToggleButton button = tabButtons.create(Images.ASTERISK.getImage(), defaultEditor, "Current Image", false);
        button.fire();
    }

    public void addPalette(PaletteFile file) {
        PaletteEditor editor = new PaletteEditor(file);
        Image preview = file.getPreview() == null ? Images.NEW.getImage() : new Image(file.getPreview().getPath());
        PaletteToggleButton button = tabButtons.create(preview, editor, file.isNew() ? "New Palette" : file.getName(), true);
        button.fire();
    }

    public boolean close() {
        return tabButtons.closeSelected();
    }

    public PaletteTabButtons getTabButtons() {
        return tabButtons;
    }

    public PalettePane getPalettePane() {
        return palettePane;
    }

    public PaletteEditor getDefaultEditor() {
        return defaultEditor;
    }

    public PaletteEditor getEditor() {
        return paletteEditor.get();
    }

    public ObjectProperty<PaletteEditor> editorProperty() {
        return paletteEditor;
    }
}
