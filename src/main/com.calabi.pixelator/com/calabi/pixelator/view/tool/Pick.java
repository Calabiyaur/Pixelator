package com.calabi.pixelator.view.tool;

public class Pick extends Tool {

    private static Pick me = new Pick();

    private Pick() {
    }

    public static Pick getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().pickColor(getMouse());
    }

    @Override public void dragPrimary() {
        getEditor().pickColor(getMouse());
    }

    @Override public void releasePrimary() {
        getEditor().pickColor(getMouse());
    }

}
