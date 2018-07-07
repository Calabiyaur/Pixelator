package main.java.view.tool;

import main.java.res.Images;
import main.java.util.ShapeUtil;
import main.java.view.ToolView;

public class Ellipse extends Tool {

    private static Ellipse me = new Ellipse();

    private Ellipse() {
        super(
                Tools.ELLIPSE,
                Images.ELLIPSE,
                Images.USE_ELLIPSE,
                15,
                16,
                true,
                false
        );
    }

    public static Ellipse getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        getEditor().getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        getEditor().getToolLayer().setPixels(ShapeUtil.getEllipsePoints(
                getToolLayer().getStart(),
                getMouse(),
                ToolView.isFillShape()));
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

}
