package com.calabi.pixelator.view.colorselection.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.paint.Color;
import javafx.stage.PopupWindow;

import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.Do;
import com.calabi.pixelator.view.palette.PaletteEditor;

public class ChangeColorButton extends Button {

    private final ObjectProperty<Color> value = new SimpleObjectProperty<>();
    private final Color leftColor;

    private final PaletteEditor editor;
    private final PopupControl popup;

    private boolean justHidden = false;

    public ChangeColorButton(PaletteFile paletteFile, Color leftColor) {
        getStyleClass().add("change-color-button");

        setValue(leftColor);

        this.leftColor = leftColor;
        editor = new PaletteEditor(paletteFile);
        editor.selectedColorProperty().addListener((ov, o, n) -> setValue(n));

        updateColor(leftColor);
        valueProperty().addListener((ov, o, n) -> updateColor(n));

        popup = createPopup(editor);
        setOnMousePressed(e -> Do.when(!justHidden && !popup.isShowing(), () -> showPopup()));
        editor.setOnMouseReleased(e -> popup.hide());
        popup.setOnAutoHide(e -> justHidden = true);
        setOnMouseReleased(e -> justHidden = false);
        focusedProperty().addListener((ov, o, n) -> Do.when(!n, () -> justHidden = false));

        setMinSize(80, 30);
    }

    private PopupControl createPopup(Node content) {
        PopupControl popup = new PopupControl() {
            {
                setSkin(new Skin<>() {
                    @Override
                    public Skinnable getSkinnable() {
                        return ChangeColorButton.this;
                    }

                    @Override
                    public Node getNode() {
                        return content;
                    }

                    @Override
                    public void dispose() {
                    }
                });
            }
        };
        popup.setConsumeAutoHidingEvents(false);
        popup.setAutoHide(true);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        return popup;
    }

    private void showPopup() {
        if (!popup.isShowing()) {
            Bounds bounds = localToScreen(getBoundsInLocal());
            double x = bounds.getMinX();
            double y = bounds.getMaxY();
            popup.show(getScene().getWindow(), x + 20, y);
        }
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