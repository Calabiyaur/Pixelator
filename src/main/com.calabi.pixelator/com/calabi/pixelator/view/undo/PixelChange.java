package com.calabi.pixelator.view.undo;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import com.calabi.pixelator.meta.PixelArray;

public class PixelChange extends PixelArray implements Undoable {

    private final PixelWriter writer;

    public PixelChange(PixelWriter writer) {
        super();
        this.writer = writer;
    }

    @Override
    public void undo() {
        forEach((x, y, previousColor, color) -> {
            writer.setColor(x, y, previousColor);
        });
    }

    @Override
    public void redo() {
        forEach((x, y, previousColor, color) -> {
            writer.setColor(x, y, color);
        });
    }

    /**
     * Add a new pixel, if it represents a change in color
     */
    @Override
    public void add(int x, int y, Color previousColor, Color color) {
        if (!color.equals(previousColor)) {
            super.add(x, y, previousColor, color);
        }
    }

    /**
     * Add a new pixel without checking for color change
     */
    public void addForcefully(int x, int y, Color previousColor, Color color) { //TODO: Shouldn't this generally be avoided?
        super.add(x, y, previousColor, color);
    }

    public void addForcefully(PixelArray other) {
        other.forEach((x, y, previousColor, color) -> {
            addForcefully(x, y, previousColor, color);
        });
    }

    @Override
    public boolean isEmpty() {
        return height() == 0;
    }

    @Override
    public PixelChange copy() {
        PixelChange pixelChange = new PixelChange(writer);
        pixelChange.addForcefully(this);
        return pixelChange;
    }

}
