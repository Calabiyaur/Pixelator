package com.calabi.pixelator.view.tool;

import javafx.scene.input.KeyCode;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.shape.RectangleHelper;

public class Select extends Tool {

    private static Select me = new Select();

    private SelectType type = SelectType.SELECT;

    Select() {
        super(
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

    public SelectType getType() {
        return type;
    }

    private static SelectType getType(KeyCode code, boolean released) {
        if (released || !getSelectionLayer().isActive() || getSelectionLayer().isDragging()) {
            return SelectType.SELECT;
        }
        switch(code) {
            case SHIFT:
                return SelectType.ADD;
            case CONTROL:
                return SelectType.SUBTRACT;
            default:
                return SelectType.SELECT;
        }
    }

    private static Images getUseImage(SelectType type) {
        switch(type) {
            case ADD:
                return Images.USE_SELECT_ADD;
            case SUBTRACT:
                return Images.USE_SELECT_SUBTRACT;
            default:
                return Images.USE_SELECT;
        }
    }

    @Override public void pressPrimary() {
        if (type == SelectType.SELECT) {
            getSelectionLayer().setStart(getMouse());
        } else {
            Check.ensure(getSelectionLayer().isActive());
            getSelectionLayer().defineShape(getSelectionLayer().getPixels());
            getSelectionLayer().setStart(getMouse());
        }
        getSelectionLayer().playBorder(false);
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
            PointArray pixels = getSelectionLayer().getPixels().clone();
            PointArray rectanglePoints = RectangleHelper.getRectanglePoints(start, getMouse(), true);
            switch(type) {
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
            getSelectionLayer().playRect(true);
        }
    }

    @Override public void keyPressPrimary(KeyCode code) {
        type = getType(code, false);
        updateUseImage();
    }

    @Override public void keyReleasePrimary(KeyCode code) {
        type = getType(code, true);
        updateUseImage();
    }

    @Override protected boolean isFlexible() {
        return type == SelectType.SELECT;
    }

    private void updateUseImage() {
        Images useImage = getUseImage(type);
        if (!useImage.equals(super.getUseImage())) {
            setUseImage(useImage);
        }
    }

    enum SelectType {
        SELECT,
        ADD,
        SUBTRACT
    }
}
