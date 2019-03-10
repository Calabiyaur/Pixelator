package main.java.view.colorselection;

public enum ColorDimension {

    RED("Red", 0, 255, 0, 1),
    GREEN("Green", 0, 255, 0, 1),
    BLUE("Blue", 0, 255, 0, 1),
    HUE("Hue", 0, 360, 0, 360),
    SATURATION("Saturation", 0, 255, 0, 1),
    BRIGHTNESS("Brightness", 0, 255, 0, 1),
    ALPHA("Alpha", 0, 255, 0, 1);

    String name;
    int minTextValue;
    int maxTextValue;
    int minSliderValue;
    int maxSliderValue;

    ColorDimension(String name, int minTextValue, int maxTextValue, int minSliderValue, int maxSliderValue) {
        this.name = name;
        this.minTextValue = minTextValue;
        this.maxTextValue = maxTextValue;
        this.minSliderValue = minSliderValue;
        this.maxSliderValue = maxSliderValue;
    }

    public String getName() {
        return name;
    }

    public int getMinTextValue() {
        return minTextValue;
    }

    public int getMaxTextValue() {
        return maxTextValue;
    }

    public int getMinSliderValue() {
        return minSliderValue;
    }

    public int getMaxSliderValue() {
        return maxSliderValue;
    }
}
