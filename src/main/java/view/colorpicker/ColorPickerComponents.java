package main.java.view.colorpicker;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import main.java.control.basic.BasicTextField;
import main.java.res.Config;
import main.java.util.ColorUtil;

final class ColorPickerComponents {

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

    private final TabButtons tabButtons = new TabButtons();
    private final CustomTextField redField = new CustomTextField();
    private final CustomTextField greenField = new CustomTextField();
    private final CustomTextField blueField = new CustomTextField();
    private final CustomTextField alphaField = new CustomTextField();
    private final Slider redSlider = new Slider(0, 1, 0);
    private final Slider greenSlider = new Slider(0, 1, 0);
    private final Slider blueSlider = new Slider(0, 1, 0);
    private final Slider alphaSlider = new Slider(0, 1, 1);

    private StringConverter<Number> stringConverter;
    private boolean convertingColorFormats = false;

    public ColorPickerComponents() {
        huePicker.hueProperty().bindBidirectional(colorPicker.hueProperty());
        colorPicker.setMinSize(80, 80);

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
        preview.setStyle("-fx-background-color: " + ColorUtil.toString(color));
        Config.putString(Config.COLOR, color.toString());
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
        // Unbind text fields
        Bindings.unbindBidirectional(redField.valueProperty(), redField.getTarget());
        Bindings.bindBidirectional(greenField.valueProperty(), green, getString255Converter());
        Bindings.bindBidirectional(blueField.valueProperty(), blue, getString255Converter());
        Bindings.bindBidirectional(alphaField.valueProperty(), alpha, getString255Converter());
        // Unbind sliders
        Bindings.bindBidirectional(redSlider.valueProperty(), red);
        Bindings.bindBidirectional(greenSlider.valueProperty(), green);
        Bindings.bindBidirectional(blueSlider.valueProperty(), blue);
        Bindings.bindBidirectional(alphaSlider.valueProperty(), alpha);

        switch(colorSpace) {
            case RGB:
                initRgb();
                break;
            case HSB:
                initHsb();
                break;
            default:
                throw new IllegalArgumentException("Unknown color space: " + colorSpace);
        }

        // Bind text fields
        Bindings.bindBidirectional(redField.valueProperty(), redField.getTarget(), redField.getConverter());
        Bindings.bindBidirectional(greenField.valueProperty(), green, getString255Converter());
        Bindings.bindBidirectional(blueField.valueProperty(), blue, getString255Converter());
        Bindings.bindBidirectional(alphaField.valueProperty(), alpha, getString255Converter());
        // Bind sliders
        Bindings.bindBidirectional(redSlider.valueProperty(), red);
        Bindings.bindBidirectional(greenSlider.valueProperty(), green);
        Bindings.bindBidirectional(blueSlider.valueProperty(), blue);
        Bindings.bindBidirectional(alphaSlider.valueProperty(), alpha);

        //switch(colorSpace) {
        //    case RGB:
        //        // Bind text fields
        //        Bindings.bindBidirectional(redField.valueProperty(), red, getString255Converter());
        //        Bindings.bindBidirectional(greenField.valueProperty(), green, getString255Converter());
        //        Bindings.bindBidirectional(blueField.valueProperty(), blue, getString255Converter());
        //        Bindings.bindBidirectional(alphaField.valueProperty(), alpha, getString255Converter());
        //        // Bind sliders
        //        Bindings.bindBidirectional(redSlider.valueProperty(), red);
        //        Bindings.bindBidirectional(greenSlider.valueProperty(), green);
        //        Bindings.bindBidirectional(blueSlider.valueProperty(), blue);
        //        Bindings.bindBidirectional(alphaSlider.valueProperty(), alpha);
        //        break;
        //    case HSB:
        //        // Bind text fields
        //        Bindings.bindBidirectional(redField.valueProperty(), hue, getString360Converter());
        //        Bindings.bindBidirectional(greenField.valueProperty(), sat, getString255Converter());
        //        Bindings.bindBidirectional(blueField.valueProperty(), bright, getString255Converter());
        //        Bindings.bindBidirectional(alphaField.valueProperty(), alpha, getString255Converter());
        //        // Bind sliders
        //        Bindings.bindBidirectional(redSlider.valueProperty(), hue);
        //        Bindings.bindBidirectional(greenSlider.valueProperty(), sat);
        //        Bindings.bindBidirectional(blueSlider.valueProperty(), bright);
        //        Bindings.bindBidirectional(alphaSlider.valueProperty(), alpha);
        //        break;
        //    default:
        //        throw new IllegalArgumentException("Unknown color space: " + colorSpace);
        //}
    }

    private void initRgb() {
        redField.setTitle("Red");
        redField.setTarget(red);
        redField.setConverter(getString255Converter());
        //...
        //TODO

        redSlider.setMax(1);
        //...
        //TODO
    }

    private void initHsb() {
        //...
        //TODO

        redSlider.setMax(360);
        //...
        //TODO
    }

    private void onChangeHex() {
        if (convertingColorFormats) {
            return;
        }
        Color color = ColorUtil.valueOf(preview.getText());
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

    public TabButtons getTabButtons() {
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
            super("", 0);
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
}
