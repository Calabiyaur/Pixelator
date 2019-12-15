package com.calabi.pixelator.view.tool;

import javafx.scene.input.KeyCode;

public class Pen extends Tool {

    private static Pen me = new Pen();

    private Pen() {
        secondary = PickSelect.getMe();
    }

    public static Pen getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().paintPoint(getMouse());
    }

    @Override public void dragPrimary() {
        int distance = getMouse().distanceMax(getMousePrevious());
        if (distance == 1) {
            getEditor().paintPoint(getMouse());
        } else if (distance > 1) {
            getEditor().paintLine(getMousePrevious(), getMouse());
        }
    }

    @Override public void releasePrimary() {
        getEditor().register();
    }

    @Override public void keyPressPrimary(KeyCode code) {
        PickSelect.getMe().keyPressPrimary(code);
    }

    @Override public void keyReleasePrimary(KeyCode code) {
        PickSelect.getMe().keyReleasePrimary(code);
    }

    @Override protected boolean isFlexible() {
        return PickSelect.getMe().isFlexible();
    }
}
