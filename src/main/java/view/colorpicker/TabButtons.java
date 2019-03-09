package main.java.view.colorpicker;

import javafx.scene.Group;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class TabButtons extends Group {

    private final ToggleButton rgb;
    private final ToggleButton hsb;

    public TabButtons() {
        ToggleGroup tg = new ToggleGroup();
        tg.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null) {
                tg.selectToggle(o);
            }
        });

        rgb = new ToggleButton("RGB");
        hsb = new ToggleButton("HSB");

        tg.getToggles().addAll(rgb, hsb);

        HBox hBox = new HBox(hsb, rgb);
        hBox.setRotate(90);
        hBox.setSpacing(6);
        getChildren().add(hBox);
    }

    public ToggleButton getRgb() {
        return rgb;
    }

    public ToggleButton getHsb() {
        return hsb;
    }
}
