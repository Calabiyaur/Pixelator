package com.calabi.pixelator.view.undo;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import com.calabi.pixelator.util.meta.PixelArray;

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

    @Override
    public void add(int x, int y, Color previousColor, Color color) {
        super.add(x, y, previousColor, color);
    }

    @Override
    public PixelChange copy() {
        PixelChange pixelChange = new PixelChange(writer);
        pixelChange.add(this);
        return pixelChange;
    }

}
