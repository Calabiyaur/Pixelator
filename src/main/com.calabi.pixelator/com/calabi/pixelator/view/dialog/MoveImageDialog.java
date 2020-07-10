package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.ui.control.BasicIntegerField;
import com.calabi.pixelator.ui.control.number.IntPattern;

public class MoveImageDialog extends BasicDialog {

    private BasicIntegerField hField;
    private BasicIntegerField vField;

    public MoveImageDialog() {
        setTitle("Move the Image");
        setOkText("Move");

        hField = new BasicIntegerField("Horizontal", 0, IntPattern.INTEGER);
        vField = new BasicIntegerField("Vertical", 0, IntPattern.INTEGER);

        addContent(hField, 0, 0);
        addContent(vField, 0, 1);
    }

    public Integer getHorizontal() {
        return hField.getValue();
    }

    public Integer getVertical() {
        return vField.getValue();
    }

    @Override public void focus() {
        hField.focus();
    }
}
