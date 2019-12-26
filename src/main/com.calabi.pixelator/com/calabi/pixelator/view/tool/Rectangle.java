package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.util.shape.RectangleHelper;
import com.calabi.pixelator.view.ToolView;

public class Rectangle extends ShapeTool {

    private static Rectangle me = new Rectangle();

    private Rectangle() {
    }

    public static Rectangle getMe() {
        return me;
    }

    @Override
    protected void update(Point start, Point end) {
        getToolLayer().setPixels(RectangleHelper.getRectanglePoints(
                start,
                end,
                ToolView.get().isFillShape()));
    }

}
