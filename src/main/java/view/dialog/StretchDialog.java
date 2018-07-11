package main.java.view.dialog;

import main.java.res.Config;
import main.java.standard.control.basic.BasicCheckBox;
import main.java.standard.control.basic.BasicDialog;
import main.java.standard.control.basic.BasicTextField;

public class StretchDialog extends BasicDialog {

    private BasicTextField widthField;
    private BasicTextField heightField;
    private BasicCheckBox keepRatio;

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

        keepRatio.setValue(Config.getBoolean(Config.STRETCH_KEEP_RATIO, true));
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

    public boolean isKeepRatio() {
        return keepRatio.getValue();
    }

}
