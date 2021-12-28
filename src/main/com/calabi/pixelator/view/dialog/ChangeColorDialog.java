package com.calabi.pixelator.view.dialog;

import javafx.scene.paint.Color;

import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.ui.control.BasicCheckBox;
import com.calabi.pixelator.ui.control.BasicIntegerField;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.util.BindingUtil;
import com.calabi.pixelator.view.colorselection.control.CustomSlider;

public class ChangeColorDialog extends PreviewDialog {

    private final BasicIntegerField hueField;
    private final CustomSlider hueSlider;
    private final BasicCheckBox relativeField;

    public ChangeColorDialog(WritableImage image) {
        super(image);
        setPrefSize(640, 480);
        setTitle("Change Color");
        setOkText("Apply");

        hueField = new BasicIntegerField("Hue", 0);
        hueField.setMinValue(0);
        hueField.setMaxValue(360);

        hueSlider = new CustomSlider();
        hueSlider.setMin(0);
        hueSlider.setMax(360);
        hueSlider.setBlockIncrement(1);

        relativeField = new BasicCheckBox("Relative", false);

        addContent(hueField, 0, 0);
        addContent(hueSlider, 1, 0);
        addContent(relativeField, 0, 1);

        BindingUtil.bindBidirectional(hueField.valueProperty(), hueSlider.valueProperty(), i -> i, Number::intValue);

        listenToUpdate(hueField, relativeField);
    }

    @Override
    public void focus() {
        hueField.focus();
        updateImage();
    }

    @Override
    protected void updateImage() {
        try {
            int hue = hueField.getValue();
            boolean relative = relativeField.getValue();

            changeColor(hue, relative);

        } catch (NumberFormatException e) {
            Logger.error(e, "Invalid number in change color dialog.");
        }
    }

    private void changeColor(int hue, boolean relative) {
        updateImage((image, reader, writer) -> {

            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = reader.getColor(i, j);
                    double oldHue = color.getHue();
                    double saturation = color.getSaturation();
                    double brightness = color.getBrightness();
                    double opacity = color.getOpacity();
                    if (!relative) {
                        color = Color.hsb((oldHue + hue) % 360, saturation, brightness, opacity);
                    } else {
                        color = Color.hsb(hue, saturation, brightness, opacity);
                    }
                    writer.setColor(i, j, color);
                }
            }
        });
    }

}
