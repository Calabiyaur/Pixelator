package com.calabi.pixelator.view.tool;

import javafx.scene.input.KeyCode;

import com.calabi.pixelator.view.ToolView;

public class Pen extends Tool {

    private static final Pen me = new Pen();

    private Pen() {
        secondary = PickSelect.getMe();
    }

    public static Pen getMe() {
        return me;
    }

    @Override
    public void pressPrimary() {
        if (ToolView.get().getThickness() == 1 || ToolView.get().getBulge() == 0) {
            getEditor().paintPoint(getMouse());
        } else {
            getEditor().paintPixel(getMouse().getX(), getMouse().getY());
        }
    }

    @Override
    public void dragPrimary() {
        int distance = getMouse().distanceMax(getMousePrevious());
        if (distance >= 1) {
            if (distance > 1 || (ToolView.get().getThickness() > 1 && ToolView.get().getBulge() != 0)) {
                getEditor().paintLine(getMousePrevious(), getMouse());
            } else {
                getEditor().paintPoint(getMouse());
            }
        }
    }

    @Override
    public void releasePrimary() {
        getEditor().register();
    }

    @Override
    public void keyPressPrimary(KeyCode code) {
        PickSelect.getMe().keyPressPrimary(code);
    }

    @Override
    public void keyReleasePrimary(KeyCode code) {
        PickSelect.getMe().keyReleasePrimary(code);
    }

    @Override
    protected boolean isFlexible() {
        return PickSelect.getMe().isFlexible();
    }

}
