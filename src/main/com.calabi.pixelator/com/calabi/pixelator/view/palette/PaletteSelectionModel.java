package com.calabi.pixelator.view.palette;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.WritableImage;

import com.calabi.pixelator.files.Files;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.res.PaletteConfig;
import com.calabi.pixelator.view.dialog.SaveRequestDialog;

final class PaletteSelectionModel {

    private final PaletteTabButtons tabButtons = new PaletteTabButtons();
    private final PalettePane palettePane = new PalettePane();

    private final PaletteEditor defaultEditor;
    private final ObjectProperty<PaletteEditor> paletteEditor = new SimpleObjectProperty<>();

    public PaletteSelectionModel() {
        paletteEditor.addListener((ov, o, n) -> palettePane.setContent(n));

        WritableImage image = new WritableImage(PaletteEditor.DEFAULT_WIDTH, PaletteEditor.DEFAULT_HEIGHT);
        PaletteFile file = new PaletteFile(null, image);
        defaultEditor = new PaletteEditor(file);
        PaletteToggleButton button = tabButtons.create(Images.ASTERISK.getImage(), "Current Image", false);
        button.setOnAction(e -> paletteEditor.set(defaultEditor));
        button.fire();
    }

    public void addPalette(PaletteFile file) {
        PaletteEditor editor = new PaletteEditor(file);
        PaletteToggleButton button = tabButtons
                .create(/*TODO: file.getPreview()*/ Images.NEW.getImage(), file.isNew() ? "New Palette" : file.getName(), true);
        button.setOnAction(e -> paletteEditor.set(editor));
        button.fire();
    }

    public void saveAndUndirty() {
        updateConfig();
        Files.get().save(getEditor().getFile());
        undirty();
    }

    public void initConfig() {
        String x = getEditor().getFile().getProperties().getProperty(PaletteConfig.SELECTED_X.name());
        String y = getEditor().getFile().getProperties().getProperty(PaletteConfig.SELECTED_Y.name());
        if (x != null && y != null) {
            getEditor().select(new Point(Integer.parseInt(x), Integer.parseInt(y)));
        }
    }

    private void updateConfig() {
        Point point = getEditor().getSelected();
        getEditor().getFile().getProperties().put(PaletteConfig.SELECTED_X.name(), Integer.toString(point.getX()));
        getEditor().getFile().getProperties().put(PaletteConfig.SELECTED_Y.name(), Integer.toString(point.getY()));
    }

    public void undirty() {
        getEditor().undirty();
    }

    public boolean closeIfClean() {
        if (tabButtons.isDefaultSelected()) {
            return false;
        }
        if (getEditor().isDirty()) {
            switch(SaveRequestDialog.display()) {
                case OK:
                    saveAndUndirty();
                    break;
                case NO:
                    Files.get().saveConfig(getEditor().getFile());
                    break;
                case CANCEL:
                    return false;
            }
        }
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
