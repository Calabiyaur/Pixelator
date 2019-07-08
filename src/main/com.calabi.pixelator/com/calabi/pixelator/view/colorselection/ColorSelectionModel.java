package com.calabi.pixelator.view.colorselection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import com.calabi.pixelator.control.basic.BasicTextField;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.MapUtil;

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

    private StringConverter<Number> stringConverter;
    private boolean convertingColorFormats = false;
    private final Map<ColorDimension, DoubleProperty> dimensionMap = MapUtil.asMap(
            new ColorDimension[] { RED, GREEN, BLUE, HUE, SATURATION, BRIGHTNESS, ALPHA },
            new DoubleProperty[] { red, green, blue, hue, sat, bright, alpha }
    );
    private final Map<Integer, StringConverter<Number>> converterMap = MapUtil.asMap(
            new Integer[] { 255, 360 },
            new StringConverter[] { getString255Converter(), getString360Converter() }
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
            Bindings.unbindBidirectional(redField.valueProperty(), redField.getTarget());
            Bindings.unbindBidirectional(greenField.valueProperty(), greenField.getTarget());
            Bindings.unbindBidirectional(blueField.valueProperty(), blueField.getTarget());
            Bindings.unbindBidirectional(alphaField.valueProperty(), alphaField.getTarget());
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

            textField.setTarget(target);
            textField.setTitle(dimension.getName());
            //TODO: textField.setMinValue(dimension.minTextValue);
            //TODO: textField.setMaxValue(dimension.maxTextValue);
            textField.setConverter(converterMap.get(dimension.getMaxTextValue()));

            slider.setTarget(target);
            slider.setMin(dimension.minSliderValue);
            slider.setMax(dimension.maxSliderValue);
            slider.setBlockIncrement((dimension.maxSliderValue - dimension.minSliderValue)
                    / (double) (dimension.maxTextValue - dimension.minTextValue));
        }

        // Bind text fields
        Bindings.bindBidirectional(redField.valueProperty(), redField.getTarget(), redField.getConverter());
        Bindings.bindBidirectional(greenField.valueProperty(), greenField.getTarget(), greenField.getConverter());
        Bindings.bindBidirectional(blueField.valueProperty(), blueField.getTarget(), blueField.getConverter());
        Bindings.bindBidirectional(alphaField.valueProperty(), alphaField.getTarget(), alphaField.getConverter());
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

    private StringConverter<Number> getString255Converter() {
        if (stringConverter == null) {
            stringConverter = new StringConverter<Number>() {
                @Override public String toString(Number object) {
                    return toString255(object);
                }

                @Override public Number fromString(String string) {
                    return parse255(string);
                }
            };
        }
        return stringConverter;
    }

    private StringConverter<Number> getString360Converter() {
        return new StringConverter<Number>() {
            @Override public String toString(Number object) {
                return Integer.toString(object.intValue());
            }

            @Override public Number fromString(String string) {
                try {
                    double value = Double.parseDouble(string);
                    return Math.min(Math.max(value, 0.0), 360.0);
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage() + "\nCannot parse " + string + ".");
                    return 0.0;
                }
            }
        };
    }

    private String toString255(Number n) {
        return Integer.toString((int) Math.round(n.doubleValue() * 255));
    }

    private double parse255(String n) {
        try {
            double value = Double.parseDouble(n) / 255;
            return Math.min(Math.max(value, 0.0), 1.0);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage() + "\nCannot parse " + n + ".");
            return 0.0;
        }
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

    public BasicTextField getRedField() {
        return redField;
    }

    public BasicTextField getGreenField() {
        return greenField;
    }

    public BasicTextField getBlueField() {
        return blueField;
    }

    public BasicTextField getAlphaField() {
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

    private class CustomTextField extends BasicTextField {

        private DoubleProperty target;
        private StringConverter<Number> converter;

        public CustomTextField() {
            super("Temp", "0");
        }

        public DoubleProperty getTarget() {
            return target;
        }

        public void setTarget(DoubleProperty target) {
            this.target = target;
        }

        public StringConverter<Number> getConverter() {
            return converter;
        }

        public void setConverter(StringConverter<Number> converter) {
            this.converter = converter;
        }
    }

    private class CustomSlider extends Slider {

        private DoubleProperty target;

        public DoubleProperty getTarget() {
            return target;
        }

        public void setTarget(DoubleProperty target) {
            this.target = target;
        }
    }
}
