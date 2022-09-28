package com.calabi.pixelator.view.dialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.calabi.pixelator.config.Images;
import com.calabi.pixelator.file.PaletteFile;
import com.calabi.pixelator.ui.control.ImageButton;
import com.calabi.pixelator.ui.image.PixelatedImageView;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.ui.parent.BasicScrollPane;
import com.calabi.pixelator.view.colorselection.control.ChangeColorButton;
import com.calabi.pixelator.view.palette.PaletteMaster;
import com.calabi.pixelator.view.palette.SortMaster;

public class ChangePaletteDialog extends BasicDialog {

    private final Map<Color, Color> colorMap = new HashMap<>();
    private final Map<Color, ChangeColorButton> buttonMap = new HashMap<>();
    private final Preview preview;

    public ChangePaletteDialog(WritableImage image, PaletteFile paletteFile) {
        preview = new Preview(image);
        setPrefSize(1200, 600);
        setTitle("Change Palette");
        setOkText("Apply");

        Preview original = new Preview(image);

        for (Preview p : Arrays.asList(original, preview)) {
            p.enable();
            p.setOnAction(e -> {
                Color color = original.getColor(e);
                ChangeColorButton button = buttonMap.get(color);
                if (button != null) {
                    button.requestFocus();
                }
            });
        }

        VBox colorButtons = new VBox();
        colorButtons.setPadding(new Insets(2));
        colorButtons.setSpacing(2);
        List<Color> leftColors = SortMaster.sortByValues(PaletteMaster.extractColors(image));
        ChangeColorButton prev = null;
        for (Color color : leftColors) {
            ChangeColorButton button = new ChangeColorButton(paletteFile, color);
            buttonMap.put(color, button);
            colorButtons.getChildren().add(button);
            button.valueProperty().addListener((ov, o, n) -> {
                colorMap.put(color, n);
                Platform.runLater(() -> updateImage()); //TODO: Buffer updates in case of e.g. automatic color assignments
            });
            if (prev != null) {
                PixelatedImageView prevImageView = prev.getEditor().getImageView();
                PixelatedImageView imageView = button.getEditor().getImageView();
                prevImageView.scaleXProperty().bindBidirectional(imageView.scaleXProperty());
                prevImageView.scaleYProperty().bindBidirectional(imageView.scaleYProperty());
            }
            prev = button;
        }
        BasicScrollPane colorButtonPane = new BasicScrollPane(colorButtons);
        colorButtonPane.setScrollByMouse(true);
        colorButtonPane.setMinWidth(80 + 4 + BasicScrollPane.BAR_BREADTH);

        ImageButton auto = new ImageButton(Images.WAND);
        ImageButton revert = new ImageButton(Images.UNDO);
        BorderPane buttons = new BorderPane();
        buttons.setLeft(auto);
        buttons.setRight(revert);
        List<ChangeColorButton> rightColors = colorButtons.getChildren().stream()
                .map(c -> ((ChangeColorButton) c)).collect(Collectors.toList());
        auto.setOnAction(e -> computeOptimalMapping(rightColors, paletteFile.getImage()));
        revert.setOnAction(e -> colorButtons.getChildren().forEach(c -> {
            ((ChangeColorButton) c).setValue(((ChangeColorButton) c).getLeftColor());
        }));

        SplitPane splitPane = new SplitPane();
        VBox center = new VBox(colorButtonPane, buttons);
        center.setAlignment(Pos.CENTER);
        splitPane.getItems().addAll(original, new HBox(center, preview));

        GridPane.setHgrow(splitPane, Priority.ALWAYS);
        GridPane.setVgrow(splitPane, Priority.ALWAYS);
        HBox.setHgrow(preview, Priority.ALWAYS);

        addContent(splitPane, 0, 0);
    }

    @Override
    public void focus() {

    }

    private void computeOptimalMapping(List<ChangeColorButton> result, WritableImage palette) {
        Set<Color> availableColors = PaletteMaster.extractColors(palette);
        if (availableColors.isEmpty()) {
            return;
        }
        for (ChangeColorButton button : result) {
            Color original = button.getLeftColor();
            Iterator<Color> available = availableColors.iterator();

            Color closest = available.next();
            while (available.hasNext()) {
                Color candidate = available.next();
                if (compare(original, candidate) < compare(original, closest)) {
                    closest = candidate;
                }
            }
            button.setValue(closest);
        }
    }

    private static double compare(Color c1, Color c2) {
        return Math.abs(c1.getRed() - c2.getRed())
                + Math.abs(c1.getGreen() - c2.getGreen())
                + Math.abs(c1.getBlue() - c2.getBlue());
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

    public WritableImage getImage() {
        return preview.getImage();
    }

}
