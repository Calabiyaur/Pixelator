package main.java.view.dialog;

import main.java.view.palette.PaletteEditor;

public class NewPaletteDialog extends NewDialog {

    public NewPaletteDialog() {
        setTitle("Create a new Palette");
        setOkText("Create");

        widthField.setValue(PaletteEditor.DEFAULT_WIDTH);
        heightField.setValue(PaletteEditor.DEFAULT_HEIGHT);
    }

}
