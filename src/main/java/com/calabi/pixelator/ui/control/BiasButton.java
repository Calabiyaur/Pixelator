package com.calabi.pixelator.ui.control;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.meta.Direction;

import static com.calabi.pixelator.util.meta.Direction.EAST;
import static com.calabi.pixelator.util.meta.Direction.NONE;
import static com.calabi.pixelator.util.meta.Direction.NORTH;
import static com.calabi.pixelator.util.meta.Direction.NORTH_EAST;
import static com.calabi.pixelator.util.meta.Direction.NORTH_WEST;
import static com.calabi.pixelator.util.meta.Direction.SOUTH;
import static com.calabi.pixelator.util.meta.Direction.SOUTH_EAST;
import static com.calabi.pixelator.util.meta.Direction.SOUTH_WEST;
import static com.calabi.pixelator.util.meta.Direction.WEST;

public class BiasButton extends GridPane {

    private Direction bias;

    public BiasButton() {
        ToggleGroup tg = new UndeselectableToggleGroup();

        ToggleButton c = new ToggleImageButton(tg, Images.ARROW_C);
        ToggleButton e = new ToggleImageButton(tg, Images.ARROW_E);
        ToggleButton ne = new ToggleImageButton(tg, Images.ARROW_NE);
        ToggleButton n = new ToggleImageButton(tg, Images.ARROW_N);
        ToggleButton nw = new ToggleImageButton(tg, Images.ARROW_NW);
        ToggleButton w = new ToggleImageButton(tg, Images.ARROW_W);
        ToggleButton sw = new ToggleImageButton(tg, Images.ARROW_SW);
        ToggleButton s = new ToggleImageButton(tg, Images.ARROW_S);
        ToggleButton se = new ToggleImageButton(tg, Images.ARROW_SE);

        addRow(0, nw, n, ne);
        addRow(1, w, c, e);
        addRow(2, sw, s, se);

        c.setOnAction(a -> bias = NONE);
        e.setOnAction(a -> bias = EAST);
        ne.setOnAction(a -> bias = NORTH_EAST);
        n.setOnAction(a -> bias = NORTH);
        nw.setOnAction(a -> bias = NORTH_WEST);
        w.setOnAction(a -> bias = WEST);
        sw.setOnAction(a -> bias = SOUTH_WEST);
        s.setOnAction(a -> bias = SOUTH);
        se.setOnAction(a -> bias = SOUTH_EAST);

        c.fire();
    }

    public Direction getValue() {
        return bias;
    }

    public void setValue(Direction bias) {
        int index = switch(bias) {
            case EAST -> 5;
            case NORTH_EAST -> 2;
            case NORTH -> 1;
            case NORTH_WEST -> 0;
            case WEST -> 3;
            case SOUTH_WEST -> 6;
            case SOUTH -> 7;
            case SOUTH_EAST -> 8;
            default -> 4;
        };
        ToggleButton button = (ToggleButton) getChildren().get(index);
        button.setSelected(true);
        button.fire();
    }

}
