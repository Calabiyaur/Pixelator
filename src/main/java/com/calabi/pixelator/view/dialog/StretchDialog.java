package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.ui.control.BasicCheckBox;
import com.calabi.pixelator.ui.control.BasicIntegerField;

public class StretchDialog extends BasicDialog {

    private final BasicIntegerField widthField;
    private final BasicIntegerField heightField;
    private final BasicCheckBox keepRatio;

    public StretchDialog(int width, int height) {
        setTitle("Stretch");
        setOkText("Stretch");

        ResizeComponents resizeComponents = new ResizeComponents(width, height);
        widthField = resizeComponents.getWidthField();
        heightField = resizeComponents.getHeightField();
        keepRatio = resizeComponents.getKeepRatio();

        addContent(resizeComponents.getWPercentField(), 0, 0);
        addContent(resizeComponents.getHPercentField(), 0, 1);
        addContent(widthField, 1, 0);
        addContent(heightField, 1, 1);
        addContent(keepRatio, 0, 2);

        keepRatio.setValue(Config.STRETCH_KEEP_RATIO.getBoolean());
    }

    @Override
    public void focus() {
        widthField.focus();
    }

    public Integer getNewWidth() {
        return widthField.getValue();
    }

    public Integer getNewHeight() {
        return heightField.getValue();
    }

    public boolean isKeepRatio() {
        return keepRatio.getValue();
    }

}
