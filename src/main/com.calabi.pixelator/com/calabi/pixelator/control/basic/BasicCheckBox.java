package com.calabi.pixelator.control.basic;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.skin.CheckBoxSkin;
import javafx.scene.layout.StackPane;

public class BasicCheckBox extends BasicControl<Boolean> {

    private CheckBox checkBox;

    public BasicCheckBox(String title) {
        this(title, false);
    }

    public BasicCheckBox(String title, boolean value) {
        this(title, null, value);
    }

    public BasicCheckBox(String title, String tail, boolean value) {
        super(title, tail, value);


        checkBox.selectedProperty().bindBidirectional(this.valueProperty());
    }

    @Override
    public Control createControl() {
        checkBox = new CheckBox() {
            {
                skinProperty().addListener((ov, o, n) -> {
                    StackPane box = (StackPane) ((CheckBoxSkin) n).getChildren().stream()
                            .filter(c -> "box".equals(c.getStyleClass().get(0))).findFirst().orElseThrow();
                    box.setOnMouseClicked(e -> super.fire());
                });

                getFrontLabel().setOnMouseClicked(e -> super.fire());
            }

            @Override
            public void fire() {
                // Do nothing.
            }
        };

        return checkBox;
    }

}
