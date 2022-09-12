package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.shape.RectangleHelper;
import com.calabi.pixelator.view.InfoView;

public class Select extends SelectionTool {

    private static final Select me = new Select();

    Select() {
        super(
                Images.USE_SELECT,
                Images.USE_SELECT_ADD,
                Images.USE_SELECT_SUBTRACT
        );
    }

    public static Select getMe() {
        return me;
    }

    @Override
    public void pressPrimary() {
        if (type.get() == SelectType.SELECT) {
            getSelectionLayer().setStart(getMouse());
        } else {
            Check.ensure(getSelectionLayer().isActive());
            getSelectionLayer().defineShape(getSelectionLayer().getPixels().toPointArray());
            getSelectionLayer().setStart(getMouse());
        }
        //getSelectionLayer().playBorder(false);
        getSelectionLayer().playRect(false);
    }

    @Override
    public void dragPrimary() {
        Point start = getSelectionLayer().getStart();
        if (start != null) {
            getSelectionLayer().setEdges(start, getMouse());
            InfoView.setSelectionSize(start, getMouse());
        }
    }

    @Override
    public void releasePrimary() {
        Point start = getSelectionLayer().getStart();
        if (start != null) {
            PointArray pixels = getSelectionLayer().getPixels().toPointArray(); //TODO: This line is normally not necessary
            PointArray rectanglePoints = RectangleHelper
                    .getRectanglePoints(start, getMouse(), true, getSelectionLayer().getImageWidth(),
                            getSelectionLayer().getImageHeight());
            switch(type.get()) {
                case ADD -> pixels.add(rectanglePoints);
                case SUBTRACT -> pixels.subtract(rectanglePoints);
                default -> pixels = rectanglePoints;
            }
            getSelectionLayer().definePixels(pixels);
        }
    }

}
