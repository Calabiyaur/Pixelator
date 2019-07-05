package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;

public class Fill extends Tool {

    private static Fill me = new Fill();

    private Fill() {
        super(
                Images.FILL,
                Images.USE_FILL,
                7,
                22,
                false,
                false
        );
        secondary = Pick.getMe();
    }

    public static Fill getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().paintFill(getMouse());
        getEditor().register();
    }

    @Override public void dragPrimary() {
        // Do nothing.
    }

    @Override public void releasePrimary() {
        // Do nothing.
    }

}
