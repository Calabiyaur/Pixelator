package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.control.basic.BasicIntegerField;

public abstract class NewDialog extends BasicDialog {

    protected BasicIntegerField widthField;
    protected BasicIntegerField heightField;

    public NewDialog() {
        widthField = new BasicIntegerField("Width", 32);
        heightField = new BasicIntegerField("Height", 32);

        addContent(widthField, 0, 0);
        addContent(heightField, 0, 1);
    }

    public Integer getNewWidth() {
        return widthField.getValue();
    }

    public Integer getNewHeight() {
        return heightField.getValue();
    }

    @Override public void focus() {
        widthField.focus();
    }

}
