package main.java.standard;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

import main.java.standard.control.ToggleImageButton;
import main.java.standard.control.basic.BasicTextField;
import main.java.util.ColorUtil;

public class ColorSelection extends GridPane {

    private ColorPicker colorPicker = new ColorPicker();
    private Rectangle colorPreview = new Rectangle(100, 50);
    private BasicTextField hexField;
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
        setHgap(6);

        colorPreview.getStyleClass().add("color-rect");
        colorPicker.setColor(getColor());

        Label title = new Label("COLOR");
        ToggleGroup tg = new ToggleGroup();
        tg.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null) {
                tg.selectToggle(o);
            }
        });
        ToggleButton rgbToggle = new ToggleImageButton(tg, "RGB");
        ToggleButton hsbToggle = new ToggleImageButton(tg, "HSB");
        hexField = new BasicTextField(null, "0x000000FF");
        hexField.getControl().setPrefWidth(100);
        hexField.setMinWidth(100);
        HBox toggleBox = new HBox(rgbToggle, hsbToggle);
        Group toggleGroup = new Group(toggleBox);
        toggleBox.setSpacing(4);
        toggleBox.setRotate(270);

        GridPane rgbGrid = createRgbGrid();
        GridPane hsbGrid = createHsbGrid();

        Pane tabPane = new StackPane();
        tabPane.getChildren().addAll(rgbGrid, hsbGrid);

        rgbToggle.setOnAction(e -> rgbGrid.toFront());
        hsbToggle.setOnAction(e -> hsbGrid.toFront());

        add(title, 1, 0);
        add(new VBox(colorPreview, hexField), 1, 1);
        add(colorPicker, 2, 0, 1, 3);
        add(toggleGroup, 0, 3);
        add(tabPane, 1, 3, 2, 1);

        GridPane.setHalignment(colorPicker, HPos.RIGHT);
        GridPane.setMargin(title, new Insets(0, 0, 12, 0));
        GridPane.setMargin(colorPicker, new Insets(0, 0, 12, 0));
        GridPane.setHgrow(tabPane, Priority.ALWAYS);
        GridPane.setHalignment(tabPane, HPos.RIGHT);
        GridPane.setValignment(toggleGroup, VPos.BOTTOM);

        bind();
        rgbToggle.fire();
    }

    private GridPane createRgbGrid() {
        GridPane rgbGrid = new GridPane();
        rgbGrid.setStyle("-fx-background-color: #f4f4f4");
        rgbGrid.setHgap(6);
        rgbGrid.addRow(0, redField, redSlider);
        GridPane.setHgrow(redSlider, Priority.ALWAYS);
        redSlider.setPrefWidth(128);
        rgbGrid.addRow(1, greenField, greenSlider);
        greenSlider.setPrefWidth(128);
        rgbGrid.addRow(2, blueField, blueSlider);
        blueSlider.setPrefWidth(128);
        rgbGrid.addRow(3, alphaField, alphaSlider);
        alphaSlider.setPrefWidth(128);

        return rgbGrid;
    }

    private GridPane createHsbGrid() {
        GridPane hsbGrid = new GridPane();
        hsbGrid.setStyle("-fx-background-color: #f4f4f4");
        hsbGrid.setHgap(6);
        hsbGrid.addRow(0, hueField, hueSlider);
        GridPane.setHgrow(hueSlider, Priority.ALWAYS);
        hueSlider.setPrefWidth(128);
        hsbGrid.addRow(1, satField, satSlider);
        satSlider.setPrefWidth(128);
        hsbGrid.addRow(2, brightField, brightSlider);
        brightSlider.setPrefWidth(128);
        return hsbGrid;
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
        hexField.valueProperty().addListener(e -> onChangeHex());
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

    public ObjectProperty<Paint> colorProperty() {
        return colorPreview.fillProperty();
    }

    private void onChangeHex() {
        if (convertingColorFormats) {
            return;
        }
        Color color = ColorUtil.valueOf(hexField.getValue());
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

    public void setColor(Color color) {
        red.set(color.getRed());
        green.set(color.getGreen());
        blue.set(color.getBlue());
        alpha.set(color.getOpacity());
    }

    private void onChangeRGB() {
        if (convertingColorFormats) {
            return;
        }
        Color color = getColor();
        convertingColorFormats = true;
        hexField.setValue(ColorUtil.toString(color));
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
        hexField.setValue(ColorUtil.toString(color));
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
        hexField.setValue(ColorUtil.toString(color));
        convertingColorFormats = false;

        updatePreview(color);
    }

    private void updatePreview(Color color) {
        colorPreview.setFill(color);
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
