package com.calabi.pixelator.view.dialog;

import java.util.Arrays;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.ui.control.BasicCheckBox;
import com.calabi.pixelator.ui.control.BasicColorField;
import com.calabi.pixelator.ui.control.BasicIntegerField;
import com.calabi.pixelator.ui.control.SwapColorButton;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.util.shape.RectangleHelper;
import com.calabi.pixelator.view.ColorView;

public class OutlineDialog extends PreviewDialog {

    private final BasicIntegerField widthField;
    private final BasicColorField colorField;
    private final BasicColorField outsideField;
    private final SwapColorButton swapColorButton;
    private final BasicCheckBox inside;
    private final BasicCheckBox solidEdges;

    public OutlineDialog(WritableImage image) {
        super(image);
        setPrefSize(640, 480);
        setTitle("Outline");
        setOkText("Apply");

        widthField = new BasicIntegerField("Width", 1);
        widthField.setMaxValue(10);
        colorField = new BasicColorField("Color", ColorView.getColor());
        outsideField = new BasicColorField("Outside color", Color.TRANSPARENT);
        swapColorButton = new SwapColorButton(colorField, outsideField);
        inside = new BasicCheckBox("Fill inside", false);
        solidEdges = new BasicCheckBox("Solid Edges", false);

        addContent(widthField, 0, 0);
        addContent(colorField, 0, 1);
        addContent(swapColorButton, 0, 2);
        addContent(outsideField, 0, 3);
        addContent(inside, 0, 4);
        addContent(solidEdges, 0, 5);

        GridPane.setHalignment(swapColorButton, HPos.RIGHT);
        GridPane.setMargin(swapColorButton, new Insets(-6, 0, -6, 0));

        for (var field : Arrays.asList(widthField, inside, solidEdges)) {
            field.getControl().setPrefWidth(80);
            field.setMinWidth(160);
        }
        for (var field : Arrays.asList(colorField, outsideField)) {
            field.getControl().setPrefWidth(52);
            field.setMinWidth(160);
        }

        addColorControl(colorField, outsideField);
        listenToUpdate(widthField, colorField, outsideField, inside, solidEdges);
    }

    @Override
    public void focus() {
        widthField.focus();
        updateImage();
    }

    @Override
    protected void updateImage() {
        try {
            int width = widthField.getValue();
            Color color = colorField.getValue();
            Color outside = outsideField.getValue();

            outline(width, color, outside, inside.getValue());

        } catch (NumberFormatException e) {
            Logger.error(e, "Invalid number in outline dialog.");
        }
    }

    private void outline(int width, Color color, Color outside, boolean inside) {
        updateImage((image, reader, writer) -> {

            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    if (inside != reader.getColor(i, j).equals(outside)) {
                        if (isNeighbor(reader, i, j, width, outside, inside)) {
                            writer.setColor(i, j, color);
                        } else if (!inside) {
                            writer.setColor(i, j, outside);
                        } else {
                            writer.setColor(i, j, reader.getColor(i, j));
                        }
                    } else {
                        writer.setColor(i, j, reader.getColor(i, j));
                    }
                }
            }
        });
    }

    private boolean isNeighbor(PixelReader reader, int x, int y, int width, Color outside, boolean inside) {
        PointArray points;
        int imageWidth = (int) getImage().getWidth();
        int imageHeight = (int) getImage().getHeight();
        if (solidEdges.getValue()) {
            points = RectangleHelper.getRectanglePoints(
                    new Point(x - width, y - width), new Point(x + width, y + width), true, imageWidth, imageHeight);
        } else {
            points = RectangleHelper.getDiamondPoints(x, y, width, imageWidth, imageHeight);
        }
        for (Point point : points.getPoints()) {
            try {
                if (inside == reader.getColor(point.getX(), point.getY()).equals(outside)) {
                    return true;
                }
            } catch (IndexOutOfBoundsException e) {
                // no points here
            }
        }
        return false;
    }
}
