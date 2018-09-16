package main.java.view.tool;

import main.java.res.Images;
import main.java.util.ShapeUtil;

public class Line extends Tool {

    private static Line me = new Line();

    private Line() {
        super(
                Images.LINE,
                Images.USE_LINE,
                15,
                16,
                true,
                false
        );
        secondary = PickSelect.getMe();
    }

    public static Line getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        getEditor().getToolLayer().setPixels(ShapeUtil.getLinePoints(getToolLayer().getStart(), getMouse()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
