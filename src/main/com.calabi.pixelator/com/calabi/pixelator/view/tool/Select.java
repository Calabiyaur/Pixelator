package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.shape.RectangleHelper;

public class Select extends SelectionTool {

    private static Select me = new Select();

    Select() {
        this(true);
    }

    Select(boolean draggableAfterClick) {
        super(
                Images.SELECT,
                Images.USE_SELECT,
                Images.USE_SELECT_ADD,
                Images.USE_SELECT_SUBTRACT,
                15,
                16,
                draggableAfterClick
        );
    }

    public static Select getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        if (type.get() == SelectType.SELECT) {
            getSelectionLayer().setStart(getMouse());
        } else {
            Check.ensure(getSelectionLayer().isActive());
            getSelectionLayer().defineShape(getSelectionLayer().getPixels());
            getSelectionLayer().setStart(getMouse());
        }
        //getSelectionLayer().playBorder(false);
        getSelectionLayer().playRect(false);
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
            PointArray pixels = getSelectionLayer().getPixels().clone(); //TODO: This line is normally not necessary
            PointArray rectanglePoints = RectangleHelper.getRectanglePoints(start, getMouse(), true);
            if (rectanglePoints == null) {
                return;
            }
            switch(type.get()) {
                case ADD:
                    pixels.addExclusive(rectanglePoints);
                    break;
                case SUBTRACT:
                    pixels.subtract(rectanglePoints);
                    break;
                default:
                    pixels = rectanglePoints;
                    break;
            }
            getSelectionLayer().definePixels(pixels);
        }
    }

}
