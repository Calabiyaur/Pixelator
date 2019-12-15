package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;

public class FillColor extends Tool {

    private static FillColor me = new FillColor();

    private FillColor() {
        super(
                Images.FILL_COLOR,
                Images.USE_FILL_COLOR,
                10,
                16
        );
        secondary = Pick.getMe();
    }

    public static FillColor getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().fillColor(getMouse());
        getEditor().register();
    }

    @Override public void dragPrimary() {
        // Do nothing.
    }

    @Override public void releasePrimary() {
        // Do nothing.
    }

}
