package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;

public class Pick extends Tool {

    private static Pick me = new Pick();

    private Pick() {
        super(
                Images.PICK,
                Images.USE_PICK,
                7,
                24
        );
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
