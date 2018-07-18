package main.java.view.tool;

import main.java.res.Images;

public class Wand extends Tool {

    private static Wand me = new Wand();

    private Wand() {
        super(
                Images.WAND,
                Images.USE_WAND,
                15,
                16,
                false,
                true
        );
    }

    public static Wand getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().selectFill(getMouse());
    }

    @Override public void dragPrimary() {
        // Do nothing.
    }

    @Override public void releasePrimary() {
        // Do nothing.
    }

}
