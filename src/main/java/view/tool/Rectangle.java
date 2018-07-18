package main.java.view.tool;

import main.java.res.Images;
import main.java.util.ShapeUtil;
import main.java.view.ToolView;

public class Rectangle extends Tool {

    private static Rectangle me = new Rectangle();

    private Rectangle() {
        super(
                Images.RECTANGLE,
                Images.USE_RECTANGLE,
                15,
                16,
                true,
                false
        );
    }

    public static Rectangle getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        getEditor().getToolLayer().setPixels(ShapeUtil.getRectanglePoints(
                getToolLayer().getStart(),
                getMouse(),
                ToolView.isFillShape()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
