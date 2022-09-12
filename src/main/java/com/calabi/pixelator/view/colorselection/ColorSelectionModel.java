package com.calabi.pixelator.view.colorselection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.ui.control.BasicNumberField;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.MapUtil;
import com.calabi.pixelator.view.colorselection.control.CustomSlider;
import com.calabi.pixelator.view.colorselection.control.CustomTextField;

import static com.calabi.pixelator.view.colorselection.ColorDimension.ALPHA;
import static com.calabi.pixelator.view.colorselection.ColorDimension.BLUE;
import static com.calabi.pixelator.view.colorselection.ColorDimension.BRIGHTNESS;
import static com.calabi.pixelator.view.colorselection.ColorDimension.GREEN;
import static com.calabi.pixelator.view.colorselection.ColorDimension.HUE;
import static com.calabi.pixelator.view.colorselection.ColorDimension.RED;
import static com.calabi.pixelator.view.colorselection.ColorDimension.SATURATION;

final class ColorSelectionModel {

    static final double INDICATOR_RADIUS = 5;
    static final double INDICATOR_WIDTH = 18;
    static final double INDICATOR_HEIGHT = 7;
    static final double INDICATOR_STROKE_WIDTH = 1.5;

    private final DoubleProperty red = new SimpleDoubleProperty(0.0);
    private final DoubleProperty green = new SimpleDoubleProperty(0.0);
    private final DoubleProperty blue = new SimpleDoubleProperty(0.0);
    private final DoubleProperty hue = new SimpleDoubleProperty(0.0);
    private final DoubleProperty sat = new SimpleDoubleProperty(0.0);
    private final DoubleProperty bright = new SimpleDoubleProperty(0.0);
    private final DoubleProperty alpha = new SimpleDoubleProperty(1.0);

    private final HuePicker huePicker = new HuePicker();
    private final ColorPicker colorPicker = new ColorPicker();
    private final ColorPreview preview = new ColorPreview();
    private final ColorTabButtons tabButtons = new ColorTabButtons();

    private final CustomTextField redField = new CustomTextField();
    private final CustomTextField greenField = new CustomTextField();
    private final CustomTextField blueField = new CustomTextField();
    private final CustomTextField alphaField = new CustomTextField();
    private final CustomSlider redSlider = new CustomSlider();
    private final CustomSlider greenSlider = new CustomSlider();
    private final CustomSlider blueSlider = new CustomSlider();
    private final CustomSlider alphaSlider = new CustomSlider();

    private boolean convertingColorFormats = false;
    private final Map<ColorDimension, DoubleProperty> dimensionMap = MapUtil.asMap(
            new ColorDimension[] { RED, GREEN, BLUE, HUE, SATURATION, BRIGHTNESS, ALPHA },
            new DoubleProperty[] { red, green, blue, hue, sat, bright, alpha }
    );

    public ColorSelectionModel() {
        huePicker.hueProperty().bindBidirectional(colorPicker.hueProperty());

        tabButtons.getRgb().setOnAction(e -> init(ColorSpace.RGB));
        tabButtons.getHsb().setOnAction(e -> init(ColorSpace.HSB));

        bind();
        tabButtons.getRgb().fire();
    }

    public Color getColor() {
        return Color.color(red.get(), green.get(), blue.get(), alpha.get());
    }

    public void setColor(Color color) {
        red.set(color.getRed());
        green.set(color.getGreen());
        blue.set(color.getBlue());
        alpha.set(color.getOpacity());
    }

    void updatePreview(Color color) {
        preview.setColor(color);
        Config.COLOR.putString(color.toString());
    }

    private void bind() {
        // Convert rgb <-> hsb <-> hex
        preview.textProperty().addListener(e -> onChangeHex());
        red.addListener(e -> onChangeRGB());
        green.addListener(e -> onChangeRGB());
        blue.addListener(e -> onChangeRGB());
        hue.addListener(e -> onChangeHSB());
        sat.addListener(e -> onChangeHSB());
        bright.addListener(e -> onChangeHSB());
        alpha.addListener(e -> onChangeAlpha());

        // Bind color picker
        Bindings.bindBidirectional(hue, colorPicker.hueProperty());
        Bindings.bindBidirectional(sat, colorPicker.satProperty());
        Bindings.bindBidirectional(bright, colorPicker.brightProperty());
        Bindings.bindBidirectional(alpha, colorPicker.alphaProperty());
    }

    private void init(ColorSpace colorSpace) {
        if (redField.getTarget() != null) {
            // Unbind text fields
            Bindings.unbindBidirectional(redField.valueProperty(), redField.getTargetAsObject());
            Bindings.unbindBidirectional(greenField.valueProperty(), greenField.getTargetAsObject());
            Bindings.unbindBidirectional(blueField.valueProperty(), blueField.getTargetAsObject());
            Bindings.unbindBidirectional(alphaField.valueProperty(), alphaField.getTargetAsObject());
            // Unbind sliders
            Bindings.unbindBidirectional(redSlider.valueProperty(), redSlider.getTarget());
            Bindings.unbindBidirectional(greenSlider.valueProperty(), greenSlider.getTarget());
            Bindings.unbindBidirectional(blueSlider.valueProperty(), blueSlider.getTarget());
            Bindings.unbindBidirectional(alphaSlider.valueProperty(), alphaSlider.getTarget());
        }

        // Change targets for text fields and sliders
        List<CustomTextField> textFields = Arrays.asList(redField, greenField, blueField, alphaField);
        List<CustomSlider> sliders = Arrays.asList(redSlider, greenSlider, blueSlider, alphaSlider);
        for (int i = 0; i < 4; i++) {
            ColorDimension dimension = colorSpace.getDimensions().get(i);
            CustomTextField textField = textFields.get(i);
            CustomSlider slider = sliders.get(i);
            DoubleProperty target = dimensionMap.get(dimension);

            double step = (dimension.maxSliderValue - dimension.minSliderValue)
                    / (double) (dimension.maxTextValue - dimension.minTextValue);

            textField.setTarget(target);
            textField.setTargetAsObject(target.asObject());
            textField.setTitle(dimension.getName());
            textField.setMinValue((double) dimension.minTextValue);
            textField.setMaxValue((double) dimension.maxTextValue);
            textField.setConversionFactor((double) dimension.maxTextValue / (double) dimension.maxSliderValue);
            textField.setStep(step);

            slider.setTarget(target);
            slider.setMin(dimension.minSliderValue);
            slider.setMax(dimension.maxSliderValue);
            slider.setBlockIncrement(step);
        }

        // Bind text fields
        Bindings.bindBidirectional(redField.valueProperty(), redField.getTargetAsObject());
        Bindings.bindBidirectional(greenField.valueProperty(), greenField.getTargetAsObject());
        Bindings.bindBidirectional(blueField.valueProperty(), blueField.getTargetAsObject());
        Bindings.bindBidirectional(alphaField.valueProperty(), alphaField.getTargetAsObject());
        // Bind sliders
        Bindings.bindBidirectional(redSlider.valueProperty(), redField.getTarget());
        Bindings.bindBidirectional(greenSlider.valueProperty(), greenField.getTarget());
        Bindings.bindBidirectional(blueSlider.valueProperty(), blueField.getTarget());
        Bindings.bindBidirectional(alphaSlider.valueProperty(), alphaField.getTarget());
    }

    private void onChangeHex() {
        if (convertingColorFormats) {
            return;
        }
        Color color = preview.getColor();
        if (color == null) {
            return;
        }
        convertingColorFormats = true;
        red.set(color.getRed());
        green.set(color.getGreen());
        blue.set(color.getBlue());
        hue.set(color.getHue());
        sat.set(color.getSaturation());
        bright.set(color.getBrightness());
        alpha.set(color.getOpacity());
        convertingColorFormats = false;

        updatePreview(color);
    }

    private void onChangeRGB() {
        if (convertingColorFormats) {
            return;
        }
        Color color = getColor();
        convertingColorFormats = true;
        preview.setText(ColorUtil.toString(color));
        hue.set(color.getHue());
        sat.set(color.getSaturation());
        bright.set(color.getBrightness());
        convertingColorFormats = false;

        updatePreview(color);
    }

    private void onChangeHSB() {
        if (convertingColorFormats) {
            return;
        }
        Color color = Color.hsb(hue.get(), sat.get(), bright.get(), alpha.get());
        convertingColorFormats = true;
        preview.setText(ColorUtil.toString(color));
        red.set(color.getRed());
        green.set(color.getGreen());
        blue.set(color.getBlue());
        convertingColorFormats = false;

        updatePreview(color);
    }

    private void onChangeAlpha() {
        if (convertingColorFormats) {
            return;
        }
        Color color = getColor();
        convertingColorFormats = true;
        preview.setText(ColorUtil.toString(color));
        convertingColorFormats = false;

        updatePreview(color);
    }

    public HuePicker getHuePicker() {
        return huePicker;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public ColorPreview getPreview() {
        return preview;
    }

    public ColorTabButtons getTabButtons() {
        return tabButtons;
    }

    public BasicNumberField<?> getRedField() {
        return redField;
    }

    public BasicNumberField<?> getGreenField() {
        return greenField;
    }

    public BasicNumberField<?> getBlueField() {
        return blueField;
    }

    public BasicNumberField<?> getAlphaField() {
        return alphaField;
    }

    public Slider getRedSlider() {
        return redSlider;
    }

    public Slider getGreenSlider() {
        return greenSlider;
    }

    public Slider getBlueSlider() {
        return blueSlider;
    }

    public Slider getAlphaSlider() {
        return alphaSlider;
    }

}
