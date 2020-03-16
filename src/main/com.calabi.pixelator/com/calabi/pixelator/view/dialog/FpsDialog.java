package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.control.basic.BasicDoubleField;
import com.calabi.pixelator.control.image.WritableImage;

public class FpsDialog extends PreviewDialog {

    private final BasicDoubleField fpsField;

    public FpsDialog(WritableImage image) {
        super(image);

        setPrefSize(640, 480);
        setTitle("FPS (Frames per second)");
        setOkText("Apply");

        fpsField = new BasicDoubleField("FPS", 1000d / image.getDelay());
        fpsField.setMinValue(1.0);
        fpsField.setMaxValue(1000.0);

        addContent(fpsField, 0, 0);

        fpsField.getControl().setPrefWidth(80);
        fpsField.setMinWidth(160);

        listenToUpdate(fpsField);
    }

    @Override
    public void focus() {
        fpsField.focus();
        updateImage();
    }

    @Override
    protected void updateImage() {
        try {
            int delay = (int) Math.round(1000d / fpsField.getValue());

            getImage().setDelay(delay);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number in outline dialog.");
        }
    }

}
