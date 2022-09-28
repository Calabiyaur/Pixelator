package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.meta.PointArray;

public class SelectColor extends SelectionTool {

    private static final SelectColor me = new SelectColor();

    private SelectColor() {
        super(
                Images.USE_SELECT_COLOR,
                Images.USE_SELECT_COLOR_ADD,
                Images.USE_SELECT_COLOR_SUBTRACT
        );
    }

    public static SelectColor getMe() {
        return me;
    }

    @Override
    public void pressPrimary() {
        PointArray pixels = getSelectionLayer().getPixels().toPointArray(); //TODO: This line is normally not necessary
        PointArray selectColorPoints = getEditor().getSelectColor(getMouse());
        if (selectColorPoints == null) {
            return;
        }
        switch(type.get()) {
            case ADD -> pixels.add(selectColorPoints);
            case SUBTRACT -> pixels.subtract(selectColorPoints);
            default -> pixels = selectColorPoints;
        }
        getSelectionLayer().definePixels(pixels);
    }

    @Override
    public void dragPrimary() {
        // Do nothing.
    }

    @Override
    public void releasePrimary() {
        // Do nothing.
    }

}
