package com.calabi.pixelator.view.tool;

import javafx.scene.input.KeyCode;

import com.calabi.pixelator.meta.Point;

public abstract class ShapeTool extends Tool {

    private boolean shiftPressed;

    @Override public void pressPrimary() {
        getToolLayer().setStart(getMouse());
    }

    @Override public void dragPrimary() {
        update();
    }

    @Override public void releasePrimary() {
        getEditor().registerToolLayer();
    }

    @Override public void keyPressPrimary(KeyCode code) {
        if (KeyCode.SHIFT.equals(code)) {
            shiftPressed = true;
            if (isActive()) {
                update();
            }
        }
    }

    @Override public void keyReleasePrimary(KeyCode code) {
        if (KeyCode.SHIFT.equals(code)) {
            shiftPressed = false;
            if (isActive()) {
                update();
            }
        }
    }

    private void update() {
        Point end;
        if (shiftPressed) {
            int startX = getToolLayer().getStart().getX();
            int startY = getToolLayer().getStart().getY();
            int relX = getMouse().getX() - startX;
            int relY = getMouse().getY() - startY;
            end = shift(startX, startY, relX, relY);
        } else {
            end = getMouse();
        }
        update(getToolLayer().getStart(), end);
    }

    protected Point shift(int startX, int startY, int relX, int relY) {
        Point end;
        if (Math.abs(relX) > Math.abs(relY)) {
            end = new Point(startX + relX, startY + Math.abs(relX) * sign(relY));
        } else {
            end = new Point(startX + Math.abs(relY) * sign(relX), startY + relY);
        }
        return end;
    }

    protected abstract void update(Point start, Point end);

    protected final int sign(int number) {
        return number >= 0 ? 1 : -1;
    }

}
