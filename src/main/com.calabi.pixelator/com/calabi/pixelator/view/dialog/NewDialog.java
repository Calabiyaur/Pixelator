package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.control.basic.BasicTextField;

public abstract class NewDialog extends BasicDialog {

    protected BasicTextField widthField;
    protected BasicTextField heightField;

    public NewDialog() {
        widthField = new BasicTextField("Width", 32);
        heightField = new BasicTextField("Height", 32);

        addContent(widthField, 0, 0);
        addContent(heightField, 0, 1);
    }

    public Integer getImageWidth() {
        try {
            return Integer.parseInt(widthField.getValue());
        } catch (NumberFormatException e) {
            System.out.println(widthField.getValue() + " is not a valid number");
            return null;
        }
    }

    public Integer getImageHeight() {
        try {
            return Integer.parseInt(heightField.getValue());
        } catch (NumberFormatException e) {
            System.out.println(heightField.getValue() + " is not a valid number");
            return null;
        }
    }

    @Override public void focus() {
        widthField.focus();
    }

}
