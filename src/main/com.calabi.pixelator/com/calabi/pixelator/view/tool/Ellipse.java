package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.ShapeUtil;
import com.calabi.pixelator.view.ToolView;

public class Ellipse extends Tool {

    private static Ellipse me = new Ellipse();

    private Ellipse() {
        super(
                Images.ELLIPSE,
                Images.USE_ELLIPSE,
                15,
                16,
                true,
                false
        );
    }

    public static Ellipse getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        getEditor().getToolLayer().setPixels(ShapeUtil.getEllipsePoints(
                getToolLayer().getStart(),
                getMouse(),
                ToolView.getInstance().isFillShape()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
