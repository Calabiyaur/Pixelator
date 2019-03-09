package main.java.view.colorpicker;

public class ColorPickerComponents {

    static final double INDICATOR_RADIUS = 5;
    static final double INDICATOR_WIDTH = 18;
    static final double INDICATOR_HEIGHT = 7;
    static final double INDICATOR_STROKE_WIDTH = 2;

    private final HuePicker huePicker = new HuePicker();
    private final ColorPicker colorPicker = new ColorPicker();
    private final ColorPreview preview = new ColorPreview();

    public ColorPickerComponents() {
        huePicker.hueProperty().bindBidirectional(colorPicker.hueProperty());
        colorPicker.setMinSize(80, 80);
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
}
