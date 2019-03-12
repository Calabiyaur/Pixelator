package main.java.view.palette;

import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PaletteToggleButton extends ToggleButton {

    public PaletteToggleButton(Image image, String text) {
        getStyleClass().add("palette-toggle-button");
        setGraphic(new ImageView(image));
        //TODO: Make button expand to the left (showing the text) on mouse over
    }
}
