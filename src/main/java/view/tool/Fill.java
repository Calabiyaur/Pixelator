package main.java.view.tool;

import main.java.res.Images;

public class Fill extends Tool {

    private static Fill me = new Fill();

    private Fill() {
        super(
                Tools.FILL,
                Images.FILL,
                Images.USE_FILL,
                7,
                22,
                false,
                false
        );
        setSecondary(Pick.getMe());
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
