package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.shape.RectangleMaker;

public class Select extends Tool {

    private static Select me = new Select();

    Select() {
        super(
                Images.SELECT,
                Images.USE_SELECT,
                15,
                16,
                true,
                true
        );
    }

    public static Select getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getSelectionLayer().setStart(getMouse());
        getSelectionLayer().playBorder(false);
        getSelectionLayer().playRect(false);
    }

    @Override public void dragPrimary() {
        Point start = getSelectionLayer().getStart();
        if (start != null) {
            getSelectionLayer().setEdges(start, getMouse());
        }
    }

    @Override public void releasePrimary() {
        Point start = getSelectionLayer().getStart();
        if (start != null) {
            getSelectionLayer().definePixels(RectangleMaker.getRectanglePoints(start, getMouse(), true));
            getSelectionLayer().playRect(true);
        }
    }

}
