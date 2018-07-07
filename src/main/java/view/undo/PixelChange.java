package main.java.view.undo;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import main.java.standard.PixelArray;

public class PixelChange extends PixelArray implements Undoable {

    private PixelWriter writer;

    public PixelChange(PixelWriter writer) {
        super();
        this.writer = writer;
    }

    @Override
    public void undo() {
        for (int i = size() - 1; i >= 0; i--) {
            writer.setColor(getX(i), getY(i), getPreviousColor(i));
        }
    }

    @Override
    public void redo() {
        for (int i = 0; i < size(); i++) {
            writer.setColor(getX(i), getY(i), getColor(i));
        }
    }

    public void setWriter(PixelWriter writer) {
        this.writer = writer;
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
    public void addForcefully(int x, int y, Color previousColor, Color color) {
        super.add(x, y, previousColor, color);
    }

    public void addForcefully(PixelArray other) {
        for (int i = 0; i < other.size(); i++) {
            addForcefully(other.getX(i), other.getY(i), other.getPreviousColor(i), other.getColor(i));
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public PixelChange clone() {
        PixelChange pixelChange = new PixelChange(writer);
        pixelChange.addForcefully(this);
        return pixelChange;
    }
}
