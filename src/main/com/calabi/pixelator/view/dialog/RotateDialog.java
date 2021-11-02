package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.ui.control.BasicIntegerField;

public class RotateDialog extends BasicDialog {

    private BasicIntegerField degreeField;

    public RotateDialog() {
        setTitle("Rotate image");
        setOkText("Rotate");

        degreeField = new BasicIntegerField("Rotate by", "°", Config.ROTATE_DEGREES.getInt());
        degreeField.setMinValue(0);
        degreeField.setMaxValue(360);
        //degreeField.setWrap(true); //TODO: New parameter: When value reaches 360, go back to 0

        addContent(degreeField, 0, 0);
    }

    @Override
    public void focus() {
        degreeField.focus();
    }

    public Integer getDegrees() {
        return degreeField.getValue();
    }

}
