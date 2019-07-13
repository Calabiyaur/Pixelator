package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.ShapeUtil;
import com.calabi.pixelator.view.ToolView;

public class Line extends Tool {

    private static Line me = new Line();

    private Line() {
        super(
                Images.LINE,
                Images.USE_LINE,
                15,
                16,
                true,
                false
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
        getEditor().getToolLayer().setPixels(ShapeUtil.getLinePoints(
                getToolLayer().getStart(),
                getMouse(),
                ToolView.getInstance().getThickness()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
