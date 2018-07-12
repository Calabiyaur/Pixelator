package main.java.util;

import javafx.scene.paint.Color;

public class ColorUtil {

    /**
     * Adds a color to another color, which mixes their RGB values, and
     * presumably makes the result more opaque.
     * -
     * NOTE: this method is not symmetric. Swapping color1 and color2 can
     * yield a different result.
     *
     * @param color1: The first, already existing color
     * @param color2: The second color, which is being added to the first.
     * @return a mixture of both colors.
     */
    public static Color addColors(Color color1, Color color2) {
        double a1 = color1.getOpacity();
        double a2 = color2.getOpacity();

        if (a2 == 0) {
            return color1;
        }

        double v = (1 - a2) * a1;
        return Color.color(
                (color1.getRed() * v + color2.getRed() * a2) / (v + a2),
                (color1.getGreen() * v + color2.getGreen() * a2) / (v + a2),
                (color1.getBlue() * v + color2.getBlue() * a2) / (v + a2),
                a1 + (1 - a1) * a2
        );
    }

    /**
     * Convert a hex String to a Color.
     *
     * @return null if String is invalid.
     */
    public static Color valueOf(String hex) {
        try {
            return Color.valueOf(hex);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert a Color to a hex String.
     *
     * @return null if String is invalid.
     */
    public static String toString(Color color) {
        return color.toString().toUpperCase().replace("0X", "#");
    }

}
