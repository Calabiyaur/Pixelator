package com.calabi.pixelator.view.tool;

public class PickSelect extends Select {

    private static PickSelect me = new PickSelect();

    private PickSelect() {
        super(Tools.PICK_SELECT);
    }

    public static PickSelect getMe() {
        return me;
    }

    @Override public void releasePrimary() {
        if (isStillSincePress() && getSelectionLayer().isEmpty()) {
            Pick.getMe().releasePrimary();
        } else {
            super.releasePrimary();
        }
    }
}
