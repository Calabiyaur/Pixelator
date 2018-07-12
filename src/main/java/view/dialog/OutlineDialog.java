package main.java.view.dialog;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import main.java.standard.Point;
import main.java.standard.PointArray;
import main.java.standard.control.SwapColorButton;
import main.java.standard.control.basic.BasicCheckBox;
import main.java.standard.control.basic.BasicColorField;
import main.java.standard.control.basic.BasicTextField;
import main.java.util.ShapeUtil;
import main.java.view.ColorView;

public class OutlineDialog extends PreviewDialog {

    private BasicTextField widthField;
    private BasicColorField colorField;
    private BasicColorField outsideField;
    private SwapColorButton swapColorButton;
    private BasicCheckBox inside;
    private BasicCheckBox solidEdges;

    public OutlineDialog(Image image) {
        super(image);
        setTitle("Outline");
        setOkText("Apply");

        widthField = new BasicTextField("Width", "1");
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

        addColorControl(colorField, outsideField);
        listenToUpdate(widthField, colorField, outsideField, inside, solidEdges);
    }

    @Override public void focus() {
        widthField.focus();
        updateImage();
    }

    @Override protected void updateImage() {
        try {
            int width = Math.min(10, Integer.parseInt(widthField.getValue()));
            Color color = colorField.getValue();
            Color outside = outsideField.getValue();

            outline(width, color, outside, inside.getValue());

        } catch (NumberFormatException e) {
            System.out.println("Invalid number in outline dialog.");
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
        if (solidEdges.getValue()) {
            points = ShapeUtil.getRectanglePoints(
                    new Point(x - width, y - width), new Point(x + width, y + width), true);
        } else {
            points = ShapeUtil.getDiamondPoints(x, y, width);
        }
        for (int i = 0; i < points.size(); i++) {
            try {
                if (inside == reader.getColor(points.getX(i), points.getY(i)).equals(outside)) {
                    return true;
                }
            } catch (IndexOutOfBoundsException e) {
                // no points here
            }
        }
        return false;
    }
}
