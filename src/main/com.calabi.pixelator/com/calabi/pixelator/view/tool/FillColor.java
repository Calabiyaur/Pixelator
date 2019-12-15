package com.calabi.pixelator.view.tool;

public class FillColor extends Tool {

    private static FillColor me = new FillColor();

    private FillColor() {
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
