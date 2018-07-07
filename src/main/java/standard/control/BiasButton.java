package main.java.standard.control;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import main.java.res.Images;
import main.java.standard.Direction;

import static main.java.standard.Direction.EAST;
import static main.java.standard.Direction.NONE;
import static main.java.standard.Direction.NORTH;
import static main.java.standard.Direction.NORTH_EAST;
import static main.java.standard.Direction.NORTH_WEST;
import static main.java.standard.Direction.SOUTH;
import static main.java.standard.Direction.SOUTH_EAST;
import static main.java.standard.Direction.SOUTH_WEST;
import static main.java.standard.Direction.WEST;

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

    public Direction getBias() {
        return bias;
    }

}
