package main.java.standard;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import main.java.res.Config;
import main.java.standard.control.basic.BasicTabPane;
import main.java.standard.control.basic.BasicTextField;
import main.java.util.ColorUtil;

public class ColorSelection extends BorderPane {

    private static final int LABEL_WIDTH = 70;
    private static final int TEXT_WIDTH = 50;
    private ColorPicker colorPicker = new ColorPicker();
    private Pane colorPreview = new Pane();
    private TextField hexField;
    private StringConverter<Number> stringConverter;
    private boolean convertingColorFormats = false;
    private DoubleProperty red = new SimpleDoubleProperty(0.0);
    private DoubleProperty green = new SimpleDoubleProperty(0.0);
    private DoubleProperty blue = new SimpleDoubleProperty(0.0);
    private DoubleProperty hue = new SimpleDoubleProperty(0.0);
    private DoubleProperty sat = new SimpleDoubleProperty(0.0);
    private DoubleProperty bright = new SimpleDoubleProperty(0.0);
    private DoubleProperty alpha = new SimpleDoubleProperty(1.0);
    private BasicTextField redField = new BasicTextField("Red", 0);
    private BasicTextField greenField = new BasicTextField("Green", 0);
    private BasicTextField blueField = new BasicTextField("Blue", 0);
    private BasicTextField hueField = new BasicTextField("Hue", 0);
    private BasicTextField satField = new BasicTextField("Saturation", 0);
    private BasicTextField brightField = new BasicTextField("Brightness", 0);
    private BasicTextField alphaField = new BasicTextField("Opacity", 255);
    private Slider redSlider = new Slider(0, 1, 0);
    private Slider greenSlider = new Slider(0, 1, 0);
    private Slider blueSlider = new Slider(0, 1, 0);
    private Slider hueSlider = new Slider(0, 360, 0);
    private Slider satSlider = new Slider(0, 1, 0);
    private Slider brightSlider = new Slider(0, 1, 0);
    private Slider alphaSlider = new Slider(0, 1, 1);

    public ColorSelection() {
        colorPreview.getStyleClass().add("color-rect");
        colorPreview.setMinSize(80, 50);
        colorPicker.setColor(Color.valueOf(Config.getString(Config.COLOR, "#000000")));
        updatePreview(getColor());

        Label title = new Label("COLOR");
        ToggleGroup tg = new ToggleGroup();
        tg.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null) {
                tg.selectToggle(o);
            }
        });
        hexField = new TextField("0x000000FF");

        BasicTabPane<ColorTab> tabPane = new BasicTabPane<>(Direction.NORTH, Direction.WEST);
        tabPane.setSpacing(0, 0, 0, 8);
        tabPane.addTab(new ColorTab(createHsbPane()), "HSB");
        ColorTab rgbTab = new ColorTab(createRgbPane());
        tabPane.addTab(rgbTab, "RGB");

        setTop(title);
        VBox vBox = new VBox(colorPreview, hexField);
        HBox colorBox = new HBox(new Group(colorPicker), vBox);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        colorBox.setSpacing(6);
        colorBox.setPadding(new Insets(6, 0, 6, 0));
        setCenter(colorBox);
        setBottom(tabPane);
        BorderPane.setMargin(colorBox, new Insets(0, 0, 0, -2));

        bind();
        rgbTab.getToggle().fire();
    }

    private Pane createRgbPane() {
        GridPane rgbGrid = new GridPane();
        rgbGrid.setStyle("-fx-background-color: #f4f4f4");
        rgbGrid.setHgap(6);
        rgbGrid.addRow(0, redField, redSlider);
        rgbGrid.addRow(1, greenField, greenSlider);
        rgbGrid.addRow(2, blueField, blueSlider);
        rgbGrid.addRow(3, alphaField, alphaSlider);

        GridPane.setHgrow(redSlider, Priority.ALWAYS);

        redField.getFrontLabel().setMinWidth(LABEL_WIDTH);
        greenField.getFrontLabel().setMinWidth(LABEL_WIDTH);
        blueField.getFrontLabel().setMinWidth(LABEL_WIDTH);
        alphaField.getFrontLabel().setMinWidth(LABEL_WIDTH);

        redField.getControl().setMaxWidth(TEXT_WIDTH);
        greenField.getControl().setMaxWidth(TEXT_WIDTH);
        blueField.getControl().setMaxWidth(TEXT_WIDTH);
        alphaField.getControl().setMaxWidth(TEXT_WIDTH);

        return rgbGrid;
    }

    private Pane createHsbPane() {
        GridPane hsbGrid = new GridPane();
        hsbGrid.setStyle("-fx-background-color: #f4f4f4");
        hsbGrid.setHgap(6);
        hsbGrid.addRow(0, hueField, hueSlider);
        hsbGrid.addRow(1, satField, satSlider);
        hsbGrid.addRow(2, brightField, brightSlider);
        GridPane.setHgrow(hueSlider, Priority.ALWAYS);

        hueField.getFrontLabel().setMinWidth(LABEL_WIDTH);
        satField.getFrontLabel().setMinWidth(LABEL_WIDTH);
        brightField.getFrontLabel().setMinWidth(LABEL_WIDTH);

        hueField.getControl().setMaxWidth(TEXT_WIDTH);
        satField.getControl().setMaxWidth(TEXT_WIDTH);
        brightField.getControl().setMaxWidth(TEXT_WIDTH);

        return new VBox(hsbGrid, new GridPane());
    }

    private void bind() {
        // Bind text fields
        Bindings.bindBidirectional(redField.valueProperty(), red, getString255Converter());
        Bindings.bindBidirectional(greenField.valueProperty(), green, getString255Converter());
        Bindings.bindBidirectional(blueField.valueProperty(), blue, getString255Converter());
        Bindings.bindBidirectional(hueField.valueProperty(), hue, getString360Converter());
        Bindings.bindBidirectional(satField.valueProperty(), sat, getString255Converter());
        Bindings.bindBidirectional(brightField.valueProperty(), bright, getString255Converter());
        Bindings.bindBidirectional(alphaField.valueProperty(), alpha, getString255Converter());

        // Bind sliders
        Bindings.bindBidirectional(redSlider.valueProperty(), red);
        Bindings.bindBidirectional(greenSlider.valueProperty(), green);
        Bindings.bindBidirectional(blueSlider.valueProperty(), blue);
        Bindings.bindBidirectional(hueSlider.valueProperty(), hue);
        Bindings.bindBidirectional(satSlider.valueProperty(), sat);
        Bindings.bindBidirectional(brightSlider.valueProperty(), bright);
        Bindings.bindBidirectional(alphaSlider.valueProperty(), alpha);

        // Convert rgb <-> hsb <-> hex
        hexField.textProperty().addListener(e -> onChangeHex());
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

    public Color getColor() {
        return Color.color(red.get(), green.get(), blue.get(), alpha.get());
    }

    public void setColor(Color color) {
        red.set(color.getRed());
        green.set(color.getGreen());
        blue.set(color.getBlue());
        alpha.set(color.getOpacity());
    }

    private void onChangeHex() {
        if (convertingColorFormats) {
            return;
        }
        Color color = ColorUtil.valueOf(hexField.getText());
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
        hexField.setText(ColorUtil.toString(color));
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
        hexField.setText(ColorUtil.toString(color));
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
        hexField.setText(ColorUtil.toString(color));
        convertingColorFormats = false;

        updatePreview(color);
    }

    private void updatePreview(Color color) {
        colorPreview.setStyle("-fx-background-color: " + ColorUtil.toString(color));
        Config.putString(Config.COLOR, color.toString());
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
}
