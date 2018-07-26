package main.java.view.dialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import main.java.control.basic.ChangeColorButton;
import main.java.control.parent.BasicScrollPane;
import main.java.view.palette.PaletteMaster;

public class ChangePaletteDialog extends PreviewDialog {

    private final Map<Color, Color> colorMap = new HashMap<>();

    public ChangePaletteDialog(Image image, Image palette) {
        super(image);
        setTitle("Change Palette");
        setOkText("Apply");

        VBox buttons = new VBox();
        buttons.setPadding(new Insets(2));
        buttons.setSpacing(2);
        List<Color> leftColors = PaletteMaster.extractAndSort(image);
        Set<Color> rightColors = new HashSet<>(PaletteMaster.extractAndSort(palette));
        for (Color color : leftColors) {
            ChangeColorButton button = new ChangeColorButton(color, rightColors);
            buttons.getChildren().add(button);
            button.valueProperty().addListener((ov, o, n) -> colorMap.put(color, n));
            listenToUpdate(button.valueProperty());
        }
        BasicScrollPane scrollPane = new BasicScrollPane(buttons);
        scrollPane.setScrollByMouse(true);
        scrollPane.setMinWidth(80 + BasicScrollPane.SCROLL_BAR_WIDTH + 4);

        Preview original = new Preview(image);
        GridPane.setMargin(original, new Insets(0, 10, 0, 0));
        addContent(original, 0, 0);
        addContent(scrollPane, 1, 0);
    }

    @Override
    public void focus() {

    }

    @Override
    protected void updateImage() {
        updateImage((image, reader, writer) -> {
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = colorMap.get(reader.getColor(i, j));
                    if (color != null) {
                        writer.setColor(i, j, color);
                    }
                }
            }
        });
    }

}
