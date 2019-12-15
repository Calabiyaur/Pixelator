package com.calabi.pixelator.view.tool;

public enum Tools {

    DRAG("drag the selection layer's pixels", false, true, true),
    ELLIPSE("draw an elliptic shape", true, false, true),
    FILL("fill an area of equal color", false, false, false),
    FILL_COLOR("change one color globally", false, false, false),
    LASSO,
    LINE("draw a line between two points", true, false, true),
    NONE,
    PEN("draw one point", false, false, false),
    PICK("pick a color", false, false, false),
    RECTANGLE("draw a rectangular shape", true, false, true),
    PICK_SELECT(null, false, true, true),
    SELECT("select a rectangular-shaped area", true, true, true),
    SELECT_COLOR("change one color globally", false, true, false),
    WAND("select an area of equal color", false, true, false);

    private String description;
    private boolean draggableAfterClick;
    private boolean selectionTool;
    private boolean secondCrosshairEnabled;

    Tools() {
    }

    Tools(String description, boolean draggableAfterClick, boolean selectionTool, boolean secondCrosshairEnabled) {
        this.description = description;
        this.draggableAfterClick = draggableAfterClick;
        this.selectionTool = selectionTool;
        this.secondCrosshairEnabled = secondCrosshairEnabled;
    }

    public String getDescription() {
        return description;
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
