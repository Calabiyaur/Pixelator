package com.calabi.pixelator.view.palette;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.files.Files;
import com.calabi.pixelator.files.ImageFile;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.view.dialog.MessageDialog;
import com.calabi.pixelator.view.dialog.NewPaletteDialog;
import com.calabi.pixelator.view.editor.ImageWindowContainer;

public class PaletteSelection extends BorderPane {

    private final PaletteSelectionModel model;
    private BooleanProperty defaultPaletteSelected = new SimpleBooleanProperty(true);

    public PaletteSelection() {
        model = new PaletteSelectionModel();
        PaletteTabButtons tabButtonBox = model.getTabButtons();
        PalettePane palettePane = model.getPalettePane();

        Label title = new Label("PALETTE");
        ImageButton create = new ImageButton(Images.NEW);
        ImageButton open = new ImageButton(Images.OPEN);
        ImageButton edit = new ImageButton(Images.EDIT);
        Arrays.asList(create, open, edit).forEach(button -> button.getStyleClass().setAll("tool-button"));
        create.setOnAction(e -> createPalette());
        open.setOnAction(e -> openPalette());
        edit.setOnAction(e -> editPalette());

        edit.setDisable(true);
        edit.disableProperty().bind(paletteSelectedProperty().not());

        GridPane titlePane = new GridPane();
        ToolBar buttonBox = new ToolBar(create, open, edit);
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

    public void createPalette() {
        NewPaletteDialog dialog = new NewPaletteDialog();
        dialog.showAndFocus();
        dialog.setOnOk(e -> {
            if (dialog.getImageWidth() == null || dialog.getImageHeight() == null) {
                return;
            }
            dialog.close();
            WritableImage image = new WritableImage(dialog.getImageWidth(), dialog.getImageHeight());
            PaletteFile file = new PaletteFile(null, image);
            addPalette(file);
            ImageWindowContainer.getInstance().addImage(file);
        });
    }

    public void openPalette() {
        List<PaletteFile> palettes = Files.get().openPalettes();
        for (PaletteFile palette : palettes) {
            addPalette(palette);
        }
    }

    public void editPalette() {
        ImageWindowContainer.getInstance().addImage(getFile());
        ImageWindowContainer.getInstance().getEditor().setCleanImage(getEditor().getCleanImage());
        ImageWindowContainer.getInstance().getEditor().updateDirty();
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
            MessageDialog dialog = new MessageDialog();
            dialog.setTitle("This is too big to be a preview!");
            dialog.setMessage("The size of a palette preview can at most be 16 x 16 pixels.");
            dialog.show();
        } else {
            getFile().setPreview(imageFile.getFile());
            getFile().setPreviewImage(imageFile.getImage());
            toggle.setGraphic(new ImageView(image));
        }
    }

    public void addPalette(PaletteFile file) {
        model.addPalette(file);
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
