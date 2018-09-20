package main.java.view.tool;

import main.java.res.Images;

public class FillSelect extends Tool {

    private static FillSelect me = new FillSelect();

    private FillSelect() {
        super(
                Images.FILL_SELECT,
                Images.USE_FILL_SELECT,
                10,
                16,
                false,
                true
        );
    }

    public static FillSelect getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().selectFillSelect(getMouse());
    }

    @Override public void dragPrimary() {
        // Do nothing.
    }

    @Override public void releasePrimary() {
        // Do nothing.
    }

}
