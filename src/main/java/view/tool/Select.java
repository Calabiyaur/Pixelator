package main.java.view.tool;

import main.java.res.Images;
import main.java.standard.Point;
import main.java.util.ShapeUtil;

public class Select extends Tool {

    private static Select me = new Select();

    private Select() {
        super(
                Tools.SELECT,
                Images.SELECT,
                Images.USE_SELECT,
                15,
                16,
                true,
                true
        );
    }

    public static Select getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getSelectionLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        Point start = getSelectionLayer().getStart();
        if (start != null) {
            getSelectionLayer().setEdges(start, getMouse());
        }
    }

    @Override public void releasePrimary() {
        Point start = getSelectionLayer().getStart();
        if (start != null) {
            getSelectionLayer().definePixels(ShapeUtil.getRectanglePoints(start, getMouse(), true));
        }
    }

    @Override public void lockAndReset() {
        getEditor().lockSelection();
    }
}
