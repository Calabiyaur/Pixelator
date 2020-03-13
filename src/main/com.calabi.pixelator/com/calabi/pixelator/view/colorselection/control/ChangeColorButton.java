package com.calabi.pixelator.view.colorselection.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import com.calabi.pixelator.control.window.Popup;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.Do;
import com.calabi.pixelator.view.palette.PaletteEditor;

public class ChangeColorButton extends Button {

    private final ObjectProperty<Color> value = new SimpleObjectProperty<>();
    private final Color leftColor;

    private final PaletteEditor editor;
    private final Popup popup;

    private boolean justHidden = false;

    public ChangeColorButton(PaletteFile paletteFile, Color leftColor) {
        getStyleClass().add("change-color-button");

        setValue(leftColor);

        this.leftColor = leftColor;
        editor = new PaletteEditor(paletteFile);
        editor.selectedColorProperty().addListener((ov, o, n) -> setValue(n));

        updateColor(leftColor);
        valueProperty().addListener((ov, o, n) -> updateColor(n));

        popup = new Popup(this, editor, Direction.SOUTH_WEST, Direction.NORTH_WEST, true);
        setOnMousePressed(e -> Do.when(!justHidden && !popup.isShowing(), () -> popup.show(20, 0)));
        editor.setOnMouseReleased(e -> popup.hide());
        popup.setOnAutoHide(e -> justHidden = true);
        setOnMouseReleased(e -> justHidden = false);
        focusedProperty().addListener((ov, o, n) -> Do.when(!n, () -> justHidden = false));

        setMinSize(80, 30);
    }

    private void updateColor(Color rightColor) {
        setStyle(String.format("-px-custom-background: %s; "
                        + "-px-custom-background-2: %s;",
                ColorUtil.toString(leftColor),
                ColorUtil.toString(rightColor)));
    }

    public Color getValue() {
        return value.get();
    }

    public ObjectProperty<Color> valueProperty() {
        return value;
    }

    public void setValue(Color value) {
        this.value.set(value);
    }

    public Color getLeftColor() {
        return leftColor;
    }

    public PaletteEditor getEditor() {
        return editor;
    }

}
