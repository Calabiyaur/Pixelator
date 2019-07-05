package com.calabi.pixelator.view.dialog;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;

import com.calabi.pixelator.control.basic.BasicCheckBox;
import com.calabi.pixelator.control.basic.BasicTextField;
import com.calabi.pixelator.control.basic.BiasButton;
import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.res.Config;

public class ResizeDialog extends BasicDialog {

    private BasicTextField widthField;
    private BasicTextField heightField;
    private BasicCheckBox keepRatio;
    private BiasButton biasButton;

    public ResizeDialog(int width, int height) {
        setTitle("Resize");
        setOkText("Resize");

        ResizeComponents resizeComponents = new ResizeComponents(width, height);
        widthField = resizeComponents.getWidthField();
        heightField = resizeComponents.getHeightField();
        keepRatio = resizeComponents.getKeepRatio();
        biasButton = resizeComponents.getBiasButton();

        addContent(resizeComponents.getWPercentField(), 0, 0);
        addContent(resizeComponents.getHPercentField(), 0, 1);
        addContent(widthField, 1, 0);
        addContent(heightField, 1, 1);
        addContent(keepRatio, 0, 2);
        addContent(biasButton, 1, 2);
        GridPane.setValignment(keepRatio, VPos.TOP);
        GridPane.setHalignment(biasButton, HPos.CENTER);

        biasButton.setValue(Direction.valueOf(Config.RESIZE_BIAS.getString()));
        keepRatio.setValue(Config.RESIZE_KEEP_RATIO.getBoolean());
    }

    @Override public void focus() {
        widthField.focus();
    }

    public Integer getNewWidth() {
        return widthField.getIntValue();
    }

    public Integer getNewHeight() {
        return heightField.getIntValue();
    }

    public Boolean isKeepRatio() {
        return keepRatio.getValue();
    }

    public Direction getBias() {
        return biasButton.getValue();
    }

}
