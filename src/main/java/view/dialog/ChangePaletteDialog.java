package main.java.view.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.image.Image;
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
        List<Color> leftColors = PaletteMaster.extractAndSort(image);
        Set<Color> rightColors = PaletteMaster.extractColors(palette);
        for (Color color : leftColors) {
            ChangeColorButton button = new ChangeColorButton(color, rightColors);
            buttons.getChildren().add(button);
            button.valueProperty().addListener((ov, o, n) -> colorMap.put(color, n));
            listenToUpdate(button.valueProperty());
        }
        BasicScrollPane scrollPane = new BasicScrollPane(buttons);

        addContent(new Preview(image), 0, 0);
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
