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

    private static final ObjectProperty<Tool> actingTool = new SimpleObjectProperty<>();
    private static Point mousePrevious;
    private static Point mouse;
    private static boolean dragging = false;
    private static MouseButton mouseButton;
    private static boolean stillSincePress = true;
    Tool secondary = this;
    private final ObjectProperty<Cursor> cursor = new SimpleObjectProperty<>();

    protected Tool() {
        if (actingTool.get() == null) {
            actingTool.set(None.getMe());
        }
        this.updateCursor(getUseImage());
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

    public static ObjectProperty<Tool> actingToolProperty() {
        return actingTool;
    }

    public static void setActingTool(Tool tool) {
        actingTool.set(tool);
        if (None.getMe() != tool) {
            Logger.log("Tool", tool.getClass().getSimpleName(), "Activated");
        }
    }

    void updateCursor(Images useImage) {
        if (useImage != null) {
            cursor.set(new ImageCursor(new Image(useImage.getUrl()), getHotspotX(), getHotspotY()));
        } else if (this == None.getMe()) {
            throw new IllegalStateException("Tool without useImage: " + this);
        }
    }

    private void updateTool() {
        if (getSelectionLayer().contains(mouse) && isFlexible()) {
            actingTool.set(Drag.getMe());
        } else if (MouseButton.SECONDARY.equals(mouseButton)) {
            actingTool.set(getSecondary());
        } else {
            actingTool.set(this);
        }
    }

    public final void press(MouseEvent e) {
        updateMouse(e);
        mouseButton = e.getButton();
        stillSincePress = true;
        updateTool();

        if (!actingTool.get().isSelectionTool() || (!getSelectionLayer().contains(mouse) && actingTool.get().isFlexible())) {
            getEditor().lockSelection();
        }

        actingTool.get().pressPrimary();
    }

    public final void move(MouseEvent e) {
        updateMouse(e);
        stillSincePress = false;
        if (!Objects.equals(mouse, mousePrevious)) {
            actingTool.get().movePrimary();
        }
    }

    public final void drag(MouseEvent e) {
        updateMouse(e);
        stillSincePress = false;
        if (!Objects.equals(mouse, mousePrevious)) {
            actingTool.get().dragPrimary();
        }
    }

    public final void release(MouseEvent e) {
        updateMouse(e);
        if (actingTool.get().isDraggableAfterClick() && !dragging && isStillSincePress()) {
            dragging = true;
            mouseButton = null;
        } else {
            imitateRelease();
        }
    }

    public final void keyPress(KeyEvent e) {
        if (actingTool.get() != None.getMe()) {
            actingTool.get().keyPressPrimary(e.getCode());
        } else {
            keyPressPrimary(e.getCode());
        }
    }

    public final void keyRelease(KeyEvent e) {
        if (actingTool.get() != None.getMe()) {
            actingTool.get().keyReleasePrimary(e.getCode());
        } else {
            keyReleasePrimary(e.getCode());
        }
    }

    public void imitateRelease() {
        actingTool.get().releasePrimary();
        setActingTool(None.getMe());
        dragging = false;
        mouseButton = null;
    }

    public abstract void pressPrimary();

    public void movePrimary() {
        if (this == actingTool.get() && isDraggableAfterClick()) {
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

    public final Images getImage() {
        return ToolManager.fromTool(this).getImage();
    }

    public Images getUseImage() {
        return ToolManager.fromTool(this).getUseImage();
    }

    private int getHotspotX() {
        return ToolManager.fromTool(this).getHotspotX();
    }

    private int getHotspotY() {
        return ToolManager.fromTool(this).getHotspotY();
    }

    public final boolean isDraggableAfterClick() {
        return ToolManager.fromTool(this).isDraggableAfterClick();
    }

    public final boolean isSelectionTool() {
        return ToolManager.fromTool(this).isSelectionTool();
    }

    public final boolean isSecondaryCrosshairEnabled() {
        return ToolManager.fromTool(this).isSecondCrosshairEnabled();
    }

    public Tool getSecondary() {
        return secondary;
    }

    public static MouseButton getMouseButton() {
        return mouseButton;
    }

    public static boolean isStillSincePress() {
        return stillSincePress;
    }

    @Override public String toString() {
        String s = this.getClass().getSimpleName() + " (" + secondary.getClass().getSimpleName() + ") ";
        if (getUseImage() != null) {
            s += getUseImage().name();
        }
        return s;
    }
}
