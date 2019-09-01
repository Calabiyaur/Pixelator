package com.calabi.pixelator.view.tool;

import javafx.scene.input.KeyCode;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.shape.RectangleMaker;

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

    @Override public void pressPrimary() {
        getSelectionLayer().setStart(getMouse());
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
            getSelectionLayer().definePixels(RectangleMaker.getRectanglePoints(start, getMouse(), true));
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

    private void updateUseImage() {
        Images useImage = getUseImage(type);
        if (!useImage.equals(super.getUseImage())) {
            setUseImage(useImage);
        }
    }

    private static SelectType getType(KeyCode code, boolean released) {
        if (released) {
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

    private enum SelectType {
        SELECT,
        ADD,
        SUBTRACT
    }
}
