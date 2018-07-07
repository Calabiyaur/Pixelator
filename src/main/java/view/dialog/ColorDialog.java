package main.java.view.dialog;

import java.util.function.Consumer;

import javafx.scene.paint.Color;

import main.java.standard.ColorSelection;
import main.java.standard.control.basic.BasicDialog;

public class ColorDialog extends BasicDialog {

    private ColorSelection colorSelection;

    public ColorDialog(Color color) {
        setTitle("Choose Color");
        setOkText("Choose");

        colorSelection = new ColorSelection();
        colorSelection.setColor(color);
        setDialogContent(colorSelection);
    }

    public Color getColor() {
        return colorSelection.getColor();
    }

    public static void chooseColor(Color initialColor, Consumer<Color> consumer) {
        ColorDialog dialog = new ColorDialog(initialColor);
        dialog.setOnOk(result -> {
            consumer.accept(dialog.getColor());
            dialog.close();
        });
        dialog.show();
    }

    @Override public void focus() {
        // Color selection doesn't need to be focused
    }
}
