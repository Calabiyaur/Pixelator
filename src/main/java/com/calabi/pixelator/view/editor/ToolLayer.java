package com.calabi.pixelator.view.editor;

import javafx.scene.image.PixelReader;

import com.calabi.pixelator.meta.PixelArray;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.ui.image.SquareStack;

public class ToolLayer extends EditorLayer {

    private SquareStack squareStack;

    public ToolLayer(int width, int height, PixelReader reader) {
        super(width, height, reader);
    }

    @Override
    void resize(int width, int height, PixelReader reader) {
        super.resize(width, height, reader);
        squareStack.resize(width, height);
    }

    public void setPixels(PointArray points) {
        squareStack.define(points);
    }

    /**
     * Return the tool layer's pixels AND clear.
     */
    public PixelArray retrievePixels() {
        PixelArray result = squareStack.getPixels().copy();
        clear();
        return result;
    }

    public void clear() {
        if (!isEmpty()) {
            getPixels().reset();
        }
        squareStack.clear();
    }

    public void setSquareStack(SquareStack squareStack) {
        this.squareStack = squareStack;
    }

}
