package com.calabi.pixelator.view.tool;

import javafx.scene.input.MouseButton;

public class Drag extends Tool {

    private static Drag me = new Drag();

    private Drag() {
        super(
                null,
                null,
                0,
                0,
                false,
                true
        );
    }

    public static Drag getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        if (MouseButton.SECONDARY.equals(getMouseButton())) {
            getEditor().paintPixels(getSelectionLayer().getPixelsTransformed());
            getEditor().register();
        } else {
            getEditor().removeSelection();
        }
        getSelectionLayer().setDragStart(getMouse());
        getSelectionLayer().playBorder(false);
    }

    @Override public void dragPrimary() {
        getSelectionLayer().dragTo(getMouse());
    }

    @Override public void releasePrimary() {
        getSelectionLayer().playBorder(true);
    }
}
