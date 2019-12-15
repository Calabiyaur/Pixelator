package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.shape.ShapeMaster;
import com.calabi.pixelator.view.ToolView;

public class Line extends Tool {

    private static Line me = new Line();

    private Line() {
        super(
                Images.LINE,
                Images.USE_LINE,
                15,
                16
        );
        secondary = PickSelect.getMe();
    }

    public static Line getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        getEditor().getToolLayer().setPixels(ShapeMaster.getLinePoints(
                getToolLayer().getStart(),
                getMouse(),
                ToolView.get().getSettings()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
