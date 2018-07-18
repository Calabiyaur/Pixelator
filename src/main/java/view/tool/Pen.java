package main.java.view.tool;

import main.java.res.Images;

public class Pen extends Tool {

    private static Pen me = new Pen();

    private Pen() {
        super(
                Images.PEN,
                Images.USE_PEN,
                6,
                25,
                false,
                false
        );
        setSecondary(PickSelect.getMe());
    }

    public static Pen getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().paintPoint(getMouse());
    }

    @Override public void dragPrimary() {
        int distance = getMouse().distanceMax(getMousePrevious());
        if (distance == 1) {
            getEditor().paintPoint(getMouse());
        } else if (distance > 1) {
            getEditor().paintLine(getMousePrevious(), getMouse());
        }
    }

    @Override public void releasePrimary() {
        getEditor().register();
    }

}