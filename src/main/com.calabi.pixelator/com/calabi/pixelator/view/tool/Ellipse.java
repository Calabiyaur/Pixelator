package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.util.shape.ShapeMaster;
import com.calabi.pixelator.view.ToolView;

public class Ellipse extends Tool {

    private static Ellipse me = new Ellipse();

    private Ellipse() {
    }

    public static Ellipse getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        getEditor().getToolLayer().setPixels(ShapeMaster.getEllipsePoints(
                getToolLayer().getStart(),
                getMouse(),
                ToolView.get().getSettings()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
