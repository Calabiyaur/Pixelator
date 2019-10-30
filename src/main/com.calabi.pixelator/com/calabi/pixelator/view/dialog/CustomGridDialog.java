package com.calabi.pixelator.view.dialog;

public class CustomGridDialog extends NewDialog {

    public CustomGridDialog() {
        setTitle("Add a new Custom Grid");
        setOkText("Add");

        widthField.setValue(4);
        heightField.setValue(4);

        widthField.setMaxValue(1023);
        heightField.setMaxValue(1023);
    }

}
