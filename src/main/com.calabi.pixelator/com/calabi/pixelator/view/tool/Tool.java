package com.calabi.pixelator.view.tool;

import java.util.Objects;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.view.editor.ImageEditor;
import com.calabi.pixelator.view.editor.SelectionLayer;
import com.calabi.pixelator.view.editor.ToolLayer;

public abstract class Tool {

    private static Tool actingTool;
    private static Point mousePrevious;
    private static Point mouse;
    private static Point mouseLastPressed;
    private static boolean dragging = false;
    private static MouseButton mouseButton;
    private static boolean stillSincePress = true;

    private Images image;
    private Images useImage;
    private int hotspotX;
    private int hotspotY;

    boolean draggableAfterClick;
    boolean selectionTool;
    Tool secondary = this;

    protected Tool(Images image, Images useImage, int hotspotX, int hotspotY, boolean draggableAfterClick,
            boolean selectionTool) {

        this.image = image;
        this.useImage = useImage;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
        this.draggableAfterClick = draggableAfterClick;
        this.selectionTool = selectionTool;

        if (actingTool == null) {
            actingTool = None.getMe();
        }
    }

    private static void updateMouse(MouseEvent e) {
        mousePrevious = mouse == null ? null : mouse.copy();
        mouse = getEditor().updateMousePosition(e);
    }

    public static SelectionLayer getSelectionLayer() {
        return getEditor().getSelectionLayer();
    }

    protected static ImageEditor getEditor() {
        return ToolManager.getEditor();
    }

    public static Point getMousePrevious() {
        return mousePrevious;
    }

    public static Point getMouse() {
        return mouse;
    }

    public static Point getMouseLastPressed() {
        return mouseLastPressed;
    }

    private void updateTool() {
        if (getSelectionLayer().contains(mouse)) {
            actingTool = Drag.getMe();
        } else if (MouseButton.SECONDARY.equals(mouseButton)) {
            actingTool = getSecondary();
        } else {
            actingTool = this;
        }
    }

    public final void press(MouseEvent e) {
        updateMouse(e);
        mouseButton = e.getButton();
        mouseLastPressed = getMouse().copy();
        stillSincePress = true;
        updateTool();

        if (!actingTool.isSelectionTool() || !getSelectionLayer().contains(mouse)) {
            if (getEditor().lockSelection()) {
                return;
            }
        }

        actingTool.pressPrimary();
    }

    public final void move(MouseEvent e) {
        updateMouse(e);
        stillSincePress = false;
        if (!Objects.equals(mouse, mousePrevious)) {
            actingTool.movePrimary();
        }
    }

    public final void drag(MouseEvent e) {
        updateMouse(e);
        stillSincePress = false;
        if (!Objects.equals(mouse, mousePrevious)) {
            actingTool.dragPrimary();
        }
    }

    public final void release(MouseEvent e) {
        updateMouse(e);
        if (actingTool.isDraggableAfterClick() && !dragging && isStillSincePress()) {
            dragging = true;
            mouseButton = null;
        } else {
            imitateRelease();
        }
    }

    private void imitateRelease() {
        actingTool.releasePrimary();
        setActingTool(None.getMe());
        dragging = false;
        mouseButton = null;
    }

    public abstract void pressPrimary();

    public void movePrimary() {
        if (this == actingTool && draggableAfterClick) {
            dragPrimary();
        }
    }

    public abstract void dragPrimary();

    public abstract void releasePrimary();

    public final void escape() {
        getSelectionLayer().clear();
        getToolLayer().clear();
        getEditor().restore();
        setActingTool(None.getMe());
    }

    public void lockAndReset() {
        imitateRelease();
        ImageEditor editor = getEditor();
        if (editor != null) {
            editor.lockSelection();
        }
    }

    public final Cursor getCursor() {
        return useImage == null ? ImageCursor.NONE : new ImageCursor(new Image(useImage.getUrl()), hotspotX, hotspotY);
    }

    public final ToolLayer getToolLayer() {
        return getEditor().getToolLayer();
    }

    public static void setActingTool(Tool tool) {
        actingTool = tool;
        if (None.getMe() != tool) {
            Logger.log("Tool", tool.getClass().getSimpleName(), "Activated");
        }
    }

    public final Images getImage() {
        return image;
    }

    public final boolean isDraggableAfterClick() {
        return draggableAfterClick;
    }

    public final boolean isSelectionTool() {
        return selectionTool;
    }

    public Tool getSecondary() {
        return secondary;
    }

    public MouseButton getMouseButton() {
        return mouseButton;
    }

    public boolean isStillSincePress() {
        return stillSincePress;
    }

}
