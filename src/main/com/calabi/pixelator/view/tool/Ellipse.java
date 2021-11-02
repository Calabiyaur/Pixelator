package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.util.shape.ShapeMaster;
import com.calabi.pixelator.view.ToolView;

public class Ellipse extends ShapeTool {

    private static Ellipse me = new Ellipse();

    private Ellipse() {
    }

    public static Ellipse getMe() {
        return me;
    }

    @Override
    protected void update(Point start, Point end) {
        getToolLayer().setPixels(ShapeMaster.getEllipsePoints(
                start,
                end,
                ToolView.get().getSettings()));
    }

}
