package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.res.Images;

public class SelectColor extends SelectionTool {

    private static SelectColor me = new SelectColor();

    private SelectColor() {
        super(
                Images.SELECT_COLOR,
                Images.USE_SELECT_COLOR,
                Images.USE_SELECT_COLOR_ADD,
                Images.USE_SELECT_COLOR_SUBTRACT
        );
        hotspotX = 8;
        draggableAfterClick = false;
    }

    public static SelectColor getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        PointArray pixels = getSelectionLayer().getPixels().clone(); //TODO: This line is normally not necessary
        PointArray selectColorPoints = getEditor().getSelectColor(getMouse());
        if (selectColorPoints == null) {
            return;
        }
        switch(type.get()) {
            case ADD:
                pixels.addExclusive(selectColorPoints);
                break;
            case SUBTRACT:
                pixels.subtract(selectColorPoints);
                break;
            default:
                pixels = selectColorPoints;
                break;
        }
        getSelectionLayer().definePixels(pixels);
    }

    @Override public void dragPrimary() {
        // Do nothing.
    }

    @Override public void releasePrimary() {
        // Do nothing.
    }

}
