package main.java.standard.control;

import javafx.scene.paint.Color;

import main.java.res.Images;
import main.java.standard.control.basic.BasicControl;

public class SwapColorButton extends ImageButton {

    private final BasicControl<Color> left;
    private final BasicControl<Color> right;

    public SwapColorButton(BasicControl<Color> left, BasicControl<Color> right) {
        super(Images.SWAP_COLOR);
        this.left = left;
        this.right = right;
        setOnAction(e -> swap());
    }

    public void swap() {
        String leftColor = left.getValue().toString();
        left.setValue(right.getValue());
        right.setValue(Color.valueOf(leftColor));
    }
}
