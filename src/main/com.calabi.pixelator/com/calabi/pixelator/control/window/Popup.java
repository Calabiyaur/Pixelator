package com.calabi.pixelator.control.window;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;

import com.calabi.pixelator.meta.Direction;

public class Popup extends PopupControl {

    private Control control;
    private Direction originAnchor;
    private AnchorLocation popupAnchor;

    public Popup(Control control, Node content, Direction originAnchor, Direction popupAnchor, boolean autoHide) {

        this.control = control;
        this.originAnchor = originAnchor;
        switch(popupAnchor) {
            case NORTH_WEST:
            case NORTH:
            case WEST:
            case NONE:
                this.popupAnchor = AnchorLocation.CONTENT_TOP_LEFT;
                break;
            case NORTH_EAST:
            case EAST:
                this.popupAnchor = AnchorLocation.CONTENT_TOP_RIGHT;
                break;
            case SOUTH_WEST:
            case SOUTH:
                this.popupAnchor = AnchorLocation.CONTENT_BOTTOM_LEFT;
                break;
            case SOUTH_EAST:
                this.popupAnchor = AnchorLocation.CONTENT_BOTTOM_RIGHT;
                break;
        }

        setSkin(new Skin<>() {
            @Override
            public Skinnable getSkinnable() {
                return control;
            }

            @Override
            public Node getNode() {
                return content;
            }

            @Override
            public void dispose() {
            }
        });

        setConsumeAutoHidingEvents(false);
        setAutoHide(autoHide);
        setAnchorLocation(this.popupAnchor);
    }

    public void show(double xOffset, double yOffset) {
        if (!isShowing()) {
            Bounds bounds = control.localToScreen(control.getBoundsInLocal());
            double x = originAnchor.isWest() ? bounds.getMinX() : bounds.getMaxX();
            double y = originAnchor.isNorth() ? bounds.getMinY() : bounds.getMaxY();
            show(control.getScene().getWindow(), x + xOffset, y + yOffset);
        }
    }

}
