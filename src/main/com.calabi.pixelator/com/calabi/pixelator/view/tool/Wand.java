package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.res.Images;

public class Wand extends SelectionTool {

    private static Wand me = new Wand();

    private Wand() {
        super(
                Images.WAND,
                Images.USE_WAND,
                Images.USE_WAND_ADD,
                Images.USE_WAND_SUBTRACT,
                15,
                16,
                false
        );
    }

    public static Wand getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        PointArray pixels = getSelectionLayer().getPixels().clone(); //TODO: This line is normally not necessary
        PointArray selectFillPoints = getEditor().getSelectFill(getMouse());
        if (selectFillPoints == null) {
            return;
        }
        switch(type.get()) {
            case ADD:
                pixels.addExclusive(selectFillPoints);
                break;
            case SUBTRACT:
                pixels.subtract(selectFillPoints);
                break;
            default:
                pixels = selectFillPoints;
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
