package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.util.meta.Point;
import com.calabi.pixelator.util.shape.ShapeMaster;
import com.calabi.pixelator.view.ToolView;

public class Rectangle extends ShapeTool {

    private static final Rectangle me = new Rectangle();

    private Rectangle() {
    }

    public static Rectangle getMe() {
        return me;
    }

    @Override
    protected void update(Point start, Point end) {
        getToolLayer().setPixels(ShapeMaster.getRectanglePoints(
                start,
                end,
                ToolView.get().getSettings()));
    }

}
