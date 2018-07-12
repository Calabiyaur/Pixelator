package main.java.view.tool;

import java.util.Objects;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import main.java.logging.Logger;
import main.java.res.Images;
import main.java.standard.Point;
import main.java.view.editor.ImageEditor;
import main.java.view.editor.ImageWindowContainer;
import main.java.view.editor.SelectionLayer;
import main.java.view.editor.ToolLayer;

public abstract class Tool {

    private static Tool actingTool;
    private static Point mousePrevious;
    private static Point mouse;
    private static Point mouseLastPressed;
    private static boolean dragging = false;
    private static MouseButton mouseButton;

    private Images image;
    private Images useImage;
    private int hotspotX;
    private int hotspotY;

    private boolean draggableAfterClick;
    private boolean selectionTool;
    private Tool secondary = this;

    protected Tool(Tools tool, Images image, Images useImage, int hotspotX, int hotspotY, boolean draggableAfterClick,
            boolean selectionTool) {

        ToolManager.setTool(tool, this);
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
        return ImageWindowContainer.getEditor();
    }

    public static Point getMousePrevious() {
        return mousePrevious;
    }

    public static void setMousePrevious(Point mousePrevious) {
        Tool.mousePrevious = mousePrevious;
    }

    public static Point getMouse() {
        return mouse;
    }

    public static void setMouse(Point mouse) {
        Tool.mouse = mouse;
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
        updateTool();

        if (!actingTool.isSelectionTool() || !getSelectionLayer().contains(mouse)) {
            if (getEditor().lockSelection()) {
                return;
            }
        }

        Logger.log("Mouse", mouseButton, "Pressed", mouse);

        actingTool.pressPrimary();
    }

    public final void move(MouseEvent e) {
        updateMouse(e);
        if (!Objects.equals(mouse, mousePrevious)) {
            actingTool.movePrimary();
        }
    }

    public final void drag(MouseEvent e) {
        updateMouse(e);
        if (!Objects.equals(mouse, mousePrevious)) {
            actingTool.dragPrimary();
        }
    }

    public final void release(MouseEvent e) {
        updateMouse(e);
        if (actingTool.isDraggableAfterClick() && !dragging && isStillSincePress()) {
            Logger.log("Mouse", mouseButton, "Released for dragging", mouse);
            dragging = true;
            mouseButton = null;
        } else {
            Logger.log("Mouse", mouseButton, "Released", mouse);
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

    public boolean isStillSincePress() {
        return getMouse().equals(getMouseLastPressed());
    }

    public final void escape() {
        getSelectionLayer().clear();
        getToolLayer().clear();
        getEditor().restore();
        setActingTool(None.getMe());
    }

    public void lockAndReset() {
        imitateRelease();
    }

    public final Cursor getCursor() {
        return useImage == null ? ImageCursor.NONE : new ImageCursor(new Image(useImage.getUrl()), hotspotX, hotspotY);
    }

    public final ToolLayer getToolLayer() {
        return getEditor().getToolLayer();
    }

    public final Tool getActingTool() {
        return actingTool;
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

    public final void setSecondary(Tool secondary) {
        this.secondary = secondary;
    }

    public MouseButton getMouseButton() {
        return mouseButton;
    }
}
