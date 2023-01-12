package com.calabi.pixelator.view.palette;

import java.io.File;
import java.util.List;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.calabi.pixelator.config.Action;
import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.config.Images;
import com.calabi.pixelator.file.Files;
import com.calabi.pixelator.file.ImageFile;
import com.calabi.pixelator.file.PaletteFile;
import com.calabi.pixelator.file.PixelFile;
import com.calabi.pixelator.main.BasicToolBar;
import com.calabi.pixelator.project.Project;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.view.dialog.MessageDialog;
import com.calabi.pixelator.view.dialog.NewPaletteDialog;
import com.calabi.pixelator.view.editor.IWC;

public class PaletteSelection extends BorderPane {

    private final PaletteSelectionModel model;
    private final BooleanProperty defaultPaletteSelected = new SimpleBooleanProperty(true);

    public PaletteSelection() {
        model = new PaletteSelectionModel();
    }

    public void init() {
        PaletteTabButtons tabButtonBox = model.getTabButtons();
        PalettePane palettePane = model.getPalettePane();

        Label title = new Label("PALETTE");

        GridPane titlePane = new GridPane();
        BasicToolBar buttonBox = new BasicToolBar();
        buttonBox.addButton(Action.NEW_PALETTE, Images.NEW);
        buttonBox.addButton(Action.OPEN_PALETTE, Images.OPEN);
        buttonBox.addButton(Action.EDIT_PALETTE, Images.EDIT);
        titlePane.add(title, 0, 0);
        titlePane.add(buttonBox, 1, 0);
        GridPane.setHgrow(title, Priority.ALWAYS);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);
        setTop(titlePane);

        setOnMouseEntered(e -> setCursor(Cursor.DEFAULT));
        setLeft(tabButtonBox);
        BorderPane.setMargin(tabButtonBox, new Insets(0, 6, 0, 0));
        setCenter(palettePane);
        BorderPane.setAlignment(palettePane, Pos.TOP_LEFT);
        VBox.setVgrow(this, Priority.ALWAYS);

        tabButtonBox.visibleProperty().bind(paletteSelectedProperty());
        palettePane.visibleProperty().bind(paletteSelectedProperty());

        model.editorProperty().addListener((ov, o, n) -> defaultPaletteSelected.set(n != null && n == getDefaultEditor()));
    }

    public void saveConfig() {
        for (Node child : model.getTabButtons().getChildren()) {
            if (child instanceof PaletteToggleButton button && button.getEditor() != model.getDefaultEditor()) {
                PaletteEditor editor = button.getEditor();
                Config.PALETTE_ZOOM_LEVEL.putDouble(editor.getFile(), editor.getImageView().getScaleX());
                Files.get().saveConfig(editor.getFile());
            }
        }
    }

    public void createPalette() {
        NewPaletteDialog dialog = new NewPaletteDialog();
        dialog.showAndFocus();
        dialog.setOnOk(e -> {
            if (dialog.getNewWidth() == null || dialog.getNewHeight() == null) {
                return;
            }
            dialog.close();
            WritableImage image = new WritableImage(dialog.getNewWidth(), dialog.getNewHeight());
            PaletteFile file = new PaletteFile(null, image);
            addPalette(file);
            IWC.get().addImage(file);
        });
    }

    public void openPalette() {
        List<PaletteFile> palettes = Files.get().openPalettes();
        for (PaletteFile palette : palettes) {
            addPalette(palette);
        }
    }

    public void editPalette() {
        editPalette(getEditor());
    }

    public static void editPalette(PaletteEditor editor) {
        IWC.get().addImage(editor.getFile());
        IWC.get().getEditor().setCleanImage(editor.getCleanImage());
        IWC.get().getEditor().updateDirty();
    }

    public void changePreview() {
        changePreview(model.getTabButtons().getSelected());
    }

    public void changePreview(PixelFile previewFile) {
        for (PaletteToggleButton toggle : model.getTabButtons().getToggles()) {
            if (toggle.getEditor().getPixelFile() == previewFile) {
                changePreview(toggle);
                return;
            }
        }
    }

    private void changePreview(PaletteToggleButton toggle) {
        ImageFile imageFile = Files.get().openSingleImage();
        if (imageFile == null) {
            return;
        }
        Image image = imageFile.getImage();
        if (image.getWidth() > 16 || image.getHeight() > 16) {
            MessageDialog dialog = new MessageDialog("This is too big to be a preview!",
                    "The size of a palette preview can at most be 16 x 16 pixels.");
            dialog.show();
        } else {
            getFile().setPreview(imageFile.getImage());
            toggle.setGraphic(new ImageView(image));
            if (!getFile().isNew()) {
                Files.get().savePreview(getFile());
            }
        }
    }

    public void addPalette(PaletteFile file) {
        model.addPalette(file);
        if (Project.active()) {
            Project.get().addOpenedPalette(file);
        }
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

    public BooleanBinding paletteSelectedProperty() {
        return model.getTabButtons().sizeProperty().greaterThan(0);
    }

    public BooleanProperty defaultPaletteSelectedProperty() {
        return defaultPaletteSelected;
    }

    public void closeCurrent() {
        model.close();
    }

}
