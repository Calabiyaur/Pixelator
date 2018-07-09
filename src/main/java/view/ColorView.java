package main.java.view;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import main.java.files.PaletteFile;
import main.java.res.Config;
import main.java.res.Images;
import main.java.standard.ColorSelection;
import main.java.standard.control.ImageButton;
import main.java.standard.control.basic.BasicWindow;
import main.java.start.ActionManager;
import main.java.start.Main;
import main.java.view.palette.PaletteSelection;

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
            box.setPadding(new Insets(BasicWindow.RESIZE_MARGIN));
            box.setAlignment(Pos.TOP_RIGHT);

            ImageButton popup = new ImageButton(Images.POPUP);
            //box.getChildren().add(popup); //TODO: Make 'Popupable' superclass
            popup.setOnAction(e -> popupAction());

            colorSelection = new ColorSelection();
            colorSelection.setAlignment(Pos.TOP_RIGHT);
            box.getChildren().add(colorSelection);
            GridPane.setHgrow(colorSelection, Priority.ALWAYS);

            box.getChildren().add(new Separator());

            paletteSelection = new PaletteSelection();
            box.getChildren().add(paletteSelection);

            initConfig();
            instance.setCenter(box);
        }
        return instance;
    }

    private static void initConfig() {
        double red = Config.getDouble(Config.RED, 1);
        double green = Config.getDouble(Config.GREEN, 1);
        double blue = Config.getDouble(Config.BLUE, 1);
        double opacity = Config.getDouble(Config.OPACITY, 1);
        setColor(Color.color(red, green, blue, opacity));
    }

    private static void popupAction() {
        Stage stage = new Stage();
        //stage.titleProperty().bind(textProperty());
        stage.getIcons().add(Images.ICON.getImage());
        Main.getStages().add(stage);

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

    public static ObjectProperty<Paint> colorProperty() {
        return colorSelection.colorProperty();
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
