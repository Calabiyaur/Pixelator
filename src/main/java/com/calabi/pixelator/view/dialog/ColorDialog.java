package com.calabi.pixelator.view.dialog;

import java.util.function.Consumer;

import javafx.scene.paint.Color;

import com.calabi.pixelator.view.colorselection.ColorSelection;

public class ColorDialog extends BasicDialog {

    private final ColorSelection colorSelection;

    public ColorDialog(Color color) {
        setTitle("Choose Color");
        setOkText("Choose");

        colorSelection = new ColorSelection();
        colorSelection.setColor(color);
        setDialogContent(colorSelection);

        setPrefWidth(308);
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

    @Override
    public void focus() {
        // Color selection doesn't need to be focused
    }

}
