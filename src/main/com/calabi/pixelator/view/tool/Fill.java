package com.calabi.pixelator.view.tool;

public class Fill extends Tool {

    private static final Fill me = new Fill();

    private Fill() {
        secondary = Pick.getMe();
    }

    public static Fill getMe() {
        return me;
    }

    @Override
    public void pressPrimary() {
        getEditor().paintFill(getMouse());
        getEditor().register();
    }

    @Override
    public void dragPrimary() {
        // Do nothing.
    }

    @Override
    public void releasePrimary() {
        // Do nothing.
    }

}
