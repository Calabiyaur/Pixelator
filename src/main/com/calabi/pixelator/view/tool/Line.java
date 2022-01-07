package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.util.shape.ShapeMaster;
import com.calabi.pixelator.view.ToolView;

public class Line extends ShapeTool {

    private static final Line me = new Line();

    private Line() {
        secondary = PickSelect.getMe();
    }

    public static Line getMe() {
        return me;
    }

    @Override
    protected Point shift(int startX, int startY, int relX, int relY) {
        double slope = (double) Math.abs(relY) / (double) Math.abs(relX);
        if (slope < 0.2) {
            return new Point(startX + relX, startY);
        } else if (slope < 0.8) {
            return new Point(startX + relX, startY + sign(relY) * (Math.abs(relX) / 2));
        } else if (slope * 0.2 > 1) {
            return new Point(startX, startY + relY);
        } else if (slope * 0.8 > 1) {
            return new Point(startX + sign(relX) * (Math.abs(relY) / 2), startY + relY);
        } else {
            return super.shift(startX, startY, relX, relY);
        }
    }

    @Override
    protected void update(Point start, Point end) {
        getEditor().getToolLayer().setPixels(ShapeMaster.getLinePoints(
                start,
                end,
                ToolView.get().getSettings()));
    }

}
