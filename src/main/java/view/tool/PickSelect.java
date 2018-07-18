package main.java.view.tool;

import main.java.res.Images;

public class PickSelect extends Tool {

    private static PickSelect me = new PickSelect();

    private PickSelect() {
        super(
                Images.SELECT,
                Images.USE_SELECT,
                15,
                16,
                false,
                false
        );
    }

    public static PickSelect getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        Select.getMe().pressPrimary();
    }

    @Override public void dragPrimary() {
        Select.getMe().dragPrimary();
    }

    @Override public void releasePrimary() {
        if (isStillSincePress() && getSelectionLayer().isEmpty()) {
            Pick.getMe().releasePrimary();
        } else {
            Select.getMe().releasePrimary();
        }
    }
}
