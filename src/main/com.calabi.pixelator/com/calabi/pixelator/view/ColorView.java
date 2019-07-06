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

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.start.ActionManager;
import com.calabi.pixelator.start.Pixelator;
import com.calabi.pixelator.view.colorselection.ColorSelection;
import com.calabi.pixelator.view.palette.PaletteSelection;

public class ColorView extends BorderPane {

    private static ColorView instance;
    private static ColorSelection colorSelection;
    private static PaletteSelection paletteSelection;

    public static ColorView getInstance() {
        if (instance == null) {
            instance = new ColorView();

            VBox box = new VBox();
            instance.setStyle("-fx-background-color: #f4f4f4");
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

            instance.setCenter(box);
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
        return colorSelection.getColor();
    }

    public static void setColor(Color color) {
        colorSelection.setColor(color);
    }

    public static void addRecentColor(Color color) {
        colorSelection.addRecentColor(color);
    }

    public static PaletteSelection getPaletteSelection() {
        return paletteSelection;
    }

    public static Image getCurrentPalette() {
        return paletteSelection.getPalette();
    }

    public static void setPaletteFile(File file) {
        paletteSelection.setFile(file);
    }

    public static String getCurrentPaletteName() {
        return paletteSelection.getFile() == null ? "New Palette" : paletteSelection.getFile().getName();
    }

    public static void addPalette(Image image) {
        paletteSelection.addPalette(new PaletteFile(null, image));
    }

}
