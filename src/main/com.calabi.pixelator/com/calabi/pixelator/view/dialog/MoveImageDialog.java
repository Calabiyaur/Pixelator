package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.control.basic.BasicTextField;

public class MoveImageDialog extends BasicDialog {

    private BasicTextField hField;
    private BasicTextField vField;

    public MoveImageDialog() {
        setTitle("Move the Image");
        setOkText("Move");

        hField = new BasicTextField("Horizontal", 0);
        vField = new BasicTextField("Vertical", 0);

        addContent(hField, 0, 0);
        addContent(vField, 0, 1);
    }

    public Integer getHorizontal() {
        return hField.getIntValue();
    }

    public Integer getVertical() {
        return vField.getIntValue();
    }

    @Override public void focus() {
        hField.focus();
    }
}
