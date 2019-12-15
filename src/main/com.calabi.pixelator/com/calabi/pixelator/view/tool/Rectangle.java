package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.util.shape.RectangleHelper;
import com.calabi.pixelator.view.ToolView;

public class Rectangle extends Tool {

    private static Rectangle me = new Rectangle();

    private Rectangle() {
    }

    public static Rectangle getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        getToolLayer().setPixels(RectangleHelper.getRectanglePoints(
                getToolLayer().getStart(),
                getMouse(),
                ToolView.get().isFillShape()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
