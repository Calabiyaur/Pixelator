package com.calabi.pixelator.view.tool;

import com.calabi.pixelator.res.Images;

public enum Tools {

    DRAG("drag the selection layer's pixels", null, null, 0, 0, false, true, true),
    ELLIPSE("draw an elliptic shape", Images.ELLIPSE, Images.USE_ELLIPSE, 15, 16, true, false, true),
    FILL("fill an area of equal color", Images.FILL, Images.USE_FILL, 7, 22, false, false, false),
    FILL_COLOR("change one color globally", Images.FILL_COLOR, Images.USE_FILL_COLOR, 10, 16, false, false, false),
    LASSO,
    LINE("draw a line between two points", Images.LINE, Images.USE_LINE, 15, 16, true, false, true),
    NONE,
    PEN("draw one point", Images.PEN, Images.USE_PEN, 6, 25, false, false, false),
    PICK("pick a color", Images.PICK, Images.USE_PICK, 7, 24, false, false, false),
    RECTANGLE("draw a rectangular shape", Images.RECTANGLE, Images.USE_RECTANGLE, 15, 16, true, false, true),
    PICK_SELECT(null, Images.SELECT, Images.USE_SELECT, 15, 16, false, true, true),
    SELECT("select a rectangular-shaped area", Images.SELECT, Images.USE_SELECT, 15, 16, true, true, true),
    SELECT_COLOR("change one color globally", Images.SELECT_COLOR, Images.USE_SELECT_COLOR, 8, 16, false, true, false),
    WAND("select an area of equal color", Images.WAND, Images.USE_WAND, 15, 16, false, true, false);

    private String description;
    private Images image;
    private Images useImage;
    private int hotspotX;
    private int hotspotY;
    private boolean draggableAfterClick;
    private boolean selectionTool;
    private boolean secondCrosshairEnabled;

    Tools() {
    }

    Tools(String description, Images image, Images useImage, int hotspotX, int hotspotY,
            boolean draggableAfterClick, boolean selectionTool, boolean secondCrosshairEnabled) {
        this.description = description;
        this.image = image;
        this.useImage = useImage;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
        this.draggableAfterClick = draggableAfterClick;
        this.selectionTool = selectionTool;
        this.secondCrosshairEnabled = secondCrosshairEnabled;
    }

    public String getDescription() {
        return description;
    }

    public Images getImage() {
        return image;
    }

    public Images getUseImage() {
        return useImage;
    }

    public int getHotspotX() {
        return hotspotX;
    }

    public int getHotspotY() {
        return hotspotY;
    }

    public boolean isDraggableAfterClick() {
        return draggableAfterClick;
    }

    public boolean isSelectionTool() {
        return selectionTool;
    }

    public boolean isSecondCrosshairEnabled() {
        return secondCrosshairEnabled;
    }
}
