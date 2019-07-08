package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.ShapeUtil;
import com.calabi.pixelator.view.ToolView;

public class Rectangle extends Tool {

    private static Rectangle me = new Rectangle();

    private Rectangle() {
        super(
                Images.RECTANGLE,
                Images.USE_RECTANGLE,
                15,
                16,
                true,
                false
        );
    }

    public static Rectangle getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        getEditor().getToolLayer().setPixels(ShapeUtil.getRectanglePoints(
                getToolLayer().getStart(),
                getMouse(),
                ToolView.getInstance().isFillShape()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
