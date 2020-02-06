package com.calabi.pixelator.view.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.calabi.pixelator.control.image.PixelatedImageView;
import com.calabi.pixelator.control.parent.BasicScrollPane;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.view.colorselection.control.ChangeColorButton;
import com.calabi.pixelator.view.palette.PaletteMaster;
import com.calabi.pixelator.view.palette.SortMaster;

public class ChangePaletteDialog extends BasicDialog {

    private final Map<Color, Color> colorMap = new HashMap<>();
    private Preview preview;

    public ChangePaletteDialog(Image image, PaletteFile paletteFile) {
        preview = new Preview(image);
        setPrefSize(1150, 600);
        setTitle("Change Palette");
        setOkText("Apply");

        VBox buttons = new VBox();
        buttons.setPadding(new Insets(2));
        buttons.setSpacing(2);
        List<Color> leftColors = SortMaster.sortByValues(PaletteMaster.extractColors(image));
        ChangeColorButton prev = null;
        for (Color color : leftColors) {
            ChangeColorButton button = new ChangeColorButton(paletteFile, color);
            buttons.getChildren().add(button);
            button.valueProperty().addListener((ov, o, n) -> {
                colorMap.put(color, n);
                Platform.runLater(() -> updateImage());
            });
            if (prev != null) {
                PixelatedImageView prevImageView = prev.getEditor().getImageView();
                PixelatedImageView imageView = button.getEditor().getImageView();
                prevImageView.scaleXProperty().bindBidirectional(imageView.scaleXProperty());
                prevImageView.scaleYProperty().bindBidirectional(imageView.scaleYProperty());
            }
            prev = button;
        }
        BasicScrollPane buttonPane = new BasicScrollPane(buttons);
        buttonPane.setScrollByMouse(true);
        buttonPane.setMinWidth(80 + 4 + BasicScrollPane.BAR_BREADTH);

        Preview original = new Preview(image);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(original, new HBox(buttonPane, preview));

        GridPane.setHgrow(splitPane, Priority.ALWAYS);
        GridPane.setVgrow(splitPane, Priority.ALWAYS);
        HBox.setHgrow(preview, Priority.ALWAYS);

        addContent(splitPane, 0, 0);
    }

    @Override
    public void focus() {

    }

    private void updateImage() {
        preview.updateImage((image, reader, writer) -> {
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

    public Image getImage() {
        return preview.getImage();
    }

}
