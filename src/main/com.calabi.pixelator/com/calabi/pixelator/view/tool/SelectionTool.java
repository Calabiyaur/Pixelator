package com.calabi.pixelator.view.tool;

import javafx.scene.input.KeyCode;

import com.calabi.pixelator.res.Images;

public abstract class SelectionTool extends Tool {

    private SelectType type = SelectType.SELECT;

    SelectionTool(Images image, Images useImage) {
        super(
                image,
                useImage,
                15,
                16,
                true,
                true
        );
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

    protected abstract Images getUseImage(SelectType type);

    @Override public final void keyPressPrimary(KeyCode code) {
        type = getType(code, false);
        updateUseImage();
    }

    @Override public final void keyReleasePrimary(KeyCode code) {
        type = getType(code, true);
        updateUseImage();
    }

    @Override protected final boolean isFlexible() {
        return type == SelectType.SELECT;
    }

    private void updateUseImage() {
        Images useImage = getUseImage(type);
        if (!useImage.equals(super.getUseImage())) {
            setUseImage(useImage);
        }
    }

    private enum SelectType {
        SELECT,
        ADD,
        SUBTRACT
    }
}
