package com.calabi.pixelator.view.tool;

import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    boolean draggableAfterClick;
    boolean selectionTool;
    Tool secondary = this;
    private Images image;
    private Images useImage;
    private int hotspotX;
    private int hotspotY;
    private ObjectProperty<Cursor> cursor = new SimpleObjectProperty<>();

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

        this.updateCursor();
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

    public static void setActingTool(Tool tool) {
        actingTool = tool;
        if (None.getMe() != tool) {
            Logger.log("Tool", tool.getClass().getSimpleName(), "Activated");
        }
    }

    private void updateCursor() {
        cursor.set(useImage == null ? ImageCursor.NONE : new ImageCursor(new Image(useImage.getUrl()), hotspotX, hotspotY));
    }

    private void updateTool() {
        if (getSelectionLayer().contains(mouse) && isFlexible()) {
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

        if (!actingTool.isSelectionTool() || (!getSelectionLayer().contains(mouse) && isFlexible())) {
            getEditor().lockSelection();
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

    public final void keyPress(KeyEvent e) {
        if (actingTool != None.getMe()) {
            actingTool.keyPressPrimary(e.getCode());
        } else {
            keyPressPrimary(e.getCode());
        }
    }

    public final void keyRelease(KeyEvent e) {
        if (actingTool != None.getMe()) {
            actingTool.keyReleasePrimary(e.getCode());
        } else {
            keyReleasePrimary(e.getCode());
        }
    }

    public void imitateRelease() {
        actingTool.releasePrimary();
        //if (isFlexible()) {
            setActingTool(None.getMe());
        //}
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

    public void keyPressPrimary(KeyCode code) {
    }

    public void keyReleasePrimary(KeyCode code) {
    }

    /**
     * @return TRUE if the tool should be able to function as DRAG / initiate a new tool action depending on the mouse click
     * position.
     */
    protected boolean isFlexible() {
        return true;
    }

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
        return cursor.get();
    }

    public ObjectProperty<Cursor> cursorProperty() {
        return cursor;
    }

    public final ToolLayer getToolLayer() {
        return getEditor().getToolLayer();
    }

    public Images getUseImage() {
        return useImage;
    }

    public void setUseImage(Images useImage) {
        this.useImage = useImage;
        updateCursor();
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
