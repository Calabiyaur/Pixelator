package main.java.view.colorselection;

import java.util.Arrays;
import java.util.List;

public enum ColorSpace {
    RGB(
            new ColorDimension[] {
                    ColorDimension.RED,
                    ColorDimension.GREEN,
                    ColorDimension.BLUE,
                    ColorDimension.ALPHA
            }
    ),
    HSB(
            new ColorDimension[] {
                    ColorDimension.HUE,
                    ColorDimension.SATURATION,
                    ColorDimension.BRIGHTNESS,
                    ColorDimension.ALPHA
            }
    );

    private final List<ColorDimension> dimensions;

    ColorSpace(ColorDimension[] s) {
        this.dimensions = Arrays.asList(s);
    }

    public List<ColorDimension> getDimensions() {
        return dimensions;
    }
}
