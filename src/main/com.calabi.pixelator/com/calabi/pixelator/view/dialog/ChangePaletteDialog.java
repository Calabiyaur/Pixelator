package com.calabi.pixelator.view.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.calabi.pixelator.control.basic.ChangeColorButton;
import com.calabi.pixelator.control.parent.BasicScrollPane;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.view.palette.PaletteMaster;
import com.calabi.pixelator.view.palette.SortMaster;

public class ChangePaletteDialog extends PreviewDialog {

    private final Map<Color, Color> colorMap = new HashMap<>();

    public ChangePaletteDialog(Image image, PaletteFile paletteFile) {
        super(image);
        setTitle("Change Palette");
        setOkText("Apply");

        VBox buttons = new VBox();
        buttons.setPadding(new Insets(2));
        buttons.setSpacing(2);
        List<Color> leftColors = SortMaster.sortByValues(PaletteMaster.extractColors(image));
        for (Color color : leftColors) {
            ChangeColorButton button = new ChangeColorButton(paletteFile, color);
            buttons.getChildren().add(button);
            button.getValue().selectedColorProperty().addListener((ov, o, n) -> colorMap.put(color, n));
            listenToUpdate(button.getValue().selectedColorProperty());
        }
        BasicScrollPane scrollPane = new BasicScrollPane(buttons);
        scrollPane.setScrollByMouse(true);
        scrollPane.setMinWidth(80 + 4 + BasicScrollPane.BAR_BREADTH);

        Preview original = new Preview(image);
        GridPane.setMargin(original, new Insets(0, 10, 0, 0));
        addContent(original, 0, 0);
        addContent(scrollPane, 1, 0);

        original.minWidthProperty().bind(Bindings.min(400,
                original.getImage().widthProperty().multiply(original.getImageView().scaleXProperty())));
        original.minHeightProperty().bind(Bindings.min(400,
                original.getImage().heightProperty().multiply(original.getImageView().scaleYProperty())));
        getPreview().minWidthProperty().bind(Bindings.min(600,
                getPreview().getImage().widthProperty().multiply(getPreview().getImageView().scaleXProperty())));
        getPreview().minHeightProperty().bind(Bindings.min(600,
                getPreview().getImage().heightProperty().multiply(getPreview().getImageView().scaleYProperty())));
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
