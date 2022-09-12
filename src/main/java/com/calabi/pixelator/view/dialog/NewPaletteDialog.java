package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.view.palette.PaletteEditor;

public class NewPaletteDialog extends NewDialog {

    public NewPaletteDialog() {
        setTitle("Create a new Palette");
        setOkText("Create");

        widthField.setValue(PaletteEditor.DEFAULT_WIDTH);
        heightField.setValue(PaletteEditor.DEFAULT_HEIGHT);
    }

}
