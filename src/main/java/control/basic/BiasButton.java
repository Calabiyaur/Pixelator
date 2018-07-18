package main.java.control.basic;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import main.java.res.Images;
import main.java.meta.Direction;

import static main.java.meta.Direction.EAST;
import static main.java.meta.Direction.NONE;
import static main.java.meta.Direction.NORTH;
import static main.java.meta.Direction.NORTH_EAST;
import static main.java.meta.Direction.NORTH_WEST;
import static main.java.meta.Direction.SOUTH;
import static main.java.meta.Direction.SOUTH_EAST;
import static main.java.meta.Direction.SOUTH_WEST;
import static main.java.meta.Direction.WEST;

public class BiasButton extends GridPane {

    private Direction bias;

    public BiasButton() {
        ToggleGroup tg = new ToggleGroup();
        tg.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null && o != null) {
                o.setSelected(true);
            }
        });
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
        int index;
        switch(bias) {
            case EAST:
                index = 5;
                break;
            case NORTH_EAST:
                index = 2;
                break;
            case NORTH:
                index = 1;
                break;
            case NORTH_WEST:
                index = 0;
                break;
            case WEST:
                index = 3;
                break;
            case SOUTH_WEST:
                index = 6;
                break;
            case SOUTH:
                index = 7;
                break;
            case SOUTH_EAST:
                index = 8;
                break;
            case NONE:
            default:
                index = 4;
        }
        ToggleButton button = (ToggleButton) getChildren().get(index);
        button.setSelected(true);
        button.fire();
    }

}
