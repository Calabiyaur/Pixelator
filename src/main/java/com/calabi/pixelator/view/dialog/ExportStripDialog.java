package com.calabi.pixelator.view.dialog;

import java.io.File;

import com.calabi.pixelator.file.Category;
import com.calabi.pixelator.ui.control.BasicCheckBox;
import com.calabi.pixelator.ui.control.BasicDirectoryField;
import com.calabi.pixelator.ui.control.BasicIntegerField;

public class ExportStripDialog extends BasicDialog {

    private final BasicCheckBox individualFilesCheckBox;
    private final BasicIntegerField hFramesField;
    private final BasicDirectoryField outputField;

    public ExportStripDialog() {
        setTitle("Export animation strip");

        individualFilesCheckBox = new BasicCheckBox("Save each frame individually", false);
        hFramesField = new BasicIntegerField("Frames per row", 1);
        outputField = new BasicDirectoryField("Output directory", Category.IMAGE.getDirectory());

        hFramesField.setMinValue(1);
        hFramesField.disableProperty().bind(individualFilesCheckBox.valueProperty());

        addContent(individualFilesCheckBox, 0, 0);
        addContent(hFramesField, 0, 1);
    }

    public boolean isIndividualFiles() {
        return individualFilesCheckBox.getValue();
    }

    public int getHFrames() {
        return hFramesField.getValue();
    }

    public File getOutput() {
        return outputField.getValue();
    }

    @Override
    public void focus() {
        hFramesField.focus();
    }

}
