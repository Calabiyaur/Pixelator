package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.res.Images;

public class Wand extends SelectionTool {

    private static final Wand me = new Wand();

    private Wand() {
        super(
                Images.USE_WAND,
                Images.USE_WAND_ADD,
                Images.USE_WAND_SUBTRACT
        );
    }

    public static Wand getMe() {
        return me;
    }

    @Override
    public void pressPrimary() {
        PointArray pixels = getSelectionLayer().getPixels().toPointArray(); //TODO: This line is normally not necessary
        PointArray selectFillPoints = getEditor().getSelectFill(getMouse());
        if (selectFillPoints == null) {
            return;
        }
        switch(type.get()) {
            case ADD -> pixels.add(selectFillPoints);
            case SUBTRACT -> pixels.subtract(selectFillPoints);
            default -> pixels = selectFillPoints;
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
