package com.calabi.pixelator.view.tool;

public class None extends Tool {

    private static final None me = new None();

    private None() {
    }

    public static None getMe() {
        return me;
    }

    @Override
    public void pressPrimary() {
        // Do nothing.
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
