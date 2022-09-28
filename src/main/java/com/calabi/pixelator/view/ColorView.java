package com.calabi.pixelator.view;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.calabi.pixelator.file.PaletteFile;
import com.calabi.pixelator.main.ActionManager;
import com.calabi.pixelator.main.Pixelator;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.ui.control.ImageButton;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.view.colorselection.ColorSelection;
import com.calabi.pixelator.view.palette.PaletteSelection;

public class ColorView extends BorderPane {

    private static ColorView instance;
    private final ColorSelection colorSelection;
    private final PaletteSelection paletteSelection;

    private ColorView() {
        VBox box = new VBox();
        box.setSpacing(6);
        box.setPadding(new Insets(6, 6, 6, 1));
        box.setAlignment(Pos.TOP_RIGHT);

        ImageButton popup = new ImageButton(Images.POPUP);
        //box.getChildren().add(popup); //TODO: Make 'Popupable' superclass
        popup.setOnAction(e -> popupAction());

        colorSelection = new ColorSelection();
        box.getChildren().add(colorSelection);

        Separator halfSeparator = new Separator();
        halfSeparator.setPadding(new Insets(0, 0, -6, 0));
        box.getChildren().add(halfSeparator);

        paletteSelection = new PaletteSelection();
        box.getChildren().add(paletteSelection);

        setCenter(box);
    }

    public static ColorView get() {
        if (instance == null) {
            instance = new ColorView();
        }
        return instance;
    }

    private static void popupAction() {
        Stage stage = new Stage();
        //stage.titleProperty().bind(textProperty());
        stage.getIcons().add(Images.ICON.getImage());
        Pixelator.getStages().add(stage);

        Pane root = new Pane(instance.getChildren().get(0));
        stage.setScene(new Scene(root));

        //root.setOnScroll(e -> getImageView().scroll(e));
        //root.setOnMouseClicked(e -> mouseClick(e));
        //root.setOnMouseMoved(e -> mouseMoved(e));
        //root.setOnMouseDragged(e -> mouseMoved(e));
        stage.getScene().setOnKeyPressed(e -> ActionManager.fire(e));

        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(e -> {
            instance.getChildren().add(root.getChildren().get(0));
            instance.setVisible(true);
        });
        stage.show();
    }

    public static Color getColor() {
        return get().colorSelection.getColor();
    }

    public static void setColor(Color color) {
        get().colorSelection.setColor(color);
    }

    public static void addRecentColor(Color color) {
        get().colorSelection.addRecentColor(color);
    }

    public static PaletteSelection getPaletteSelection() {
        return get().paletteSelection;
    }

    public static Image getCurrentPalette() {
        return get().paletteSelection.getPalette();
    }

    public static void setPaletteFile(File file) {
        get().paletteSelection.setFile(file);
    }

    public static String getCurrentPaletteName() {
        return get().paletteSelection.getFile() == null ? "New Palette" : get().paletteSelection.getFile().getName();
    }

    public static void addPalette(WritableImage image) {
        PaletteFile file = new PaletteFile(null, image);
        get().paletteSelection.addPalette(file);
    }

}
