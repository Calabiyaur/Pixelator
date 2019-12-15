package com.calabi.pixelator.view.tool;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;

import com.calabi.pixelator.res.Images;

public abstract class SelectionTool extends Tool {

    protected ObjectProperty<SelectType> type = new SimpleObjectProperty<>(SelectType.SELECT);

    private Images useImageSelect;
    private Images useImageAdd;
    private Images useImageSubtract;

    SelectionTool(Tools tool,
            Images image, Images useImage, Images useImageAdd, Images useImageSubtract, int hotspotX, int hotspotY) {
        super(
                image,
                useImage,
                hotspotX,
                hotspotY
        );
        this.useImageSelect = useImage;
        this.useImageAdd = useImageAdd;
        this.useImageSubtract = useImageSubtract;
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

    private Images getUseImage(SelectType type) {
        switch(type) {
            case ADD:
                return useImageAdd;
            case SUBTRACT:
                return useImageSubtract;
            default:
                return useImageSelect;
        }
    }

    @Override public final void keyPressPrimary(KeyCode code) {
        type.set(getType(code, false));
        updateUseImage();
    }

    @Override public final void keyReleasePrimary(KeyCode code) {
        type.set(getType(code, true));
        updateUseImage();
    }

    @Override protected final boolean isFlexible() {
        return type.get() == SelectType.SELECT;
    }

    private void updateUseImage() {
        Images useImage = getUseImage(type.get());
        if (!useImage.equals(super.getUseImage())) {
            setUseImage(useImage);
        }
    }

    public ObjectProperty<SelectType> typeProperty() {
        return type;
    }

    @Override public String toString() {
        return super.toString() + ", type = " + type.get().name();
    }

    public enum SelectType {
        SELECT,
        ADD,
        SUBTRACT
    }
}
