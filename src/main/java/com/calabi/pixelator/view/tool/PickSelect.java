package com.calabi.pixelator.view.tool;

public class PickSelect extends Select {

    private static final PickSelect me = new PickSelect();

    private PickSelect() {
    }

    public static PickSelect getMe() {
        return me;
    }

    @Override
    public void releasePrimary() {
        if (isStillSincePress() && getSelectionLayer().isEmpty()) {
            Pick.getMe().releasePrimary();
        } else {
            super.releasePrimary();
        }
    }

}
