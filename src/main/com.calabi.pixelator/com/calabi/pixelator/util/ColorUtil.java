package com.calabi.pixelator.util;

import javafx.scene.paint.Color;

public final class ColorUtil {

    /**
     * Adds a color to another color, which mixes their RGB values, and
     * presumably makes the result more opaque.
     * -
     * NOTE: this method is not symmetric. Swapping color1 and color2 can
     * yield a different result.
     *
     * @param color1: The first, already existing color
     * @param color2: The second color, which is being added to the first.
     * @param replace: If TRUE, ignore color1.
     * @param alphaOnly: If TRUE, ignore the red, green and blue values of color2.
     * @return a mixture of both colors.
     */
    public static Color addColors(Color color1, Color color2, boolean replace, boolean alphaOnly) {
        double a1 = color1.getOpacity();
        double a2 = color2.getOpacity();

        if ((a2 == 0 && !replace)
                || (alphaOnly && replace && a1 == a2)) {
            return color1;
        }
        if (replace) {
            if (alphaOnly) {
                return Color.color(color1.getRed(), color1.getGreen(), color1.getBlue(), a2);
            } else {
                return color2;
            }
        } else {
            double v = (1 - a2) * a1;
            double alpha = a1 + (1 - a1) * a2;
            if (alphaOnly) {
                return Color.color(color1.getRed(), color1.getGreen(), color1.getBlue(), alpha);
            } else {
                return Color.color(
                        (color1.getRed() * v + color2.getRed() * a2) / (v + a2),
                        (color1.getGreen() * v + color2.getGreen() * a2) / (v + a2),
                        (color1.getBlue() * v + color2.getBlue() * a2) / (v + a2),
                        alpha
                );
            }
        }
    }

    /**
     * Convert a hex String to a Color.
     *
     * @return null if String is invalid.
     */
    public static Color valueOf(String hex) {
        if (hex == null) {
            return null;
        }
        try {
            return Color.valueOf(hex);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert a Color to a hex String.
     */
    public static String toString(Color color) {
        return color.toString().toUpperCase().replace("0X", "#");
    }

    /**
     * Return the color's luminosity value.
     * Range: 0 - 1
     */
    public static double getLuminosity(Color color) {
        return Math.sqrt(.241 * color.getRed() + .691 * color.getGreen() + .068 * color.getBlue());
    }

    /**
     * Return a color with a high contrast to the given color.
     * Used for displaying text on arbitrarily colored backgrounds.
     */
    public static Color getHighContrast(Color color) {
        double lum = getLuminosity(color);
        double opa = color.getOpacity();
        return opa < 0.3 ? Color.BLACK : lum < 0.8 ? Color.WHITE : Color.BLACK;
    }

    /**
     * Return a random color that is not too gray, nor too sharp.
     */
    public static Color getRandomPleasant() {
        double hue = Math.random() * 360;
        double sat = Math.random() * 0.75 + 0.25;
        double bri = Math.random() * 0.875 + 0.125;

        return Color.hsb(hue, sat, bri);
    }

    /**
     * Invert the given color.
     */
    public static Color invert(Color color) {
        return Color.color(
                1 - color.getRed(),
                1 - color.getGreen(),
                1 - color.getBlue(),
                color.getOpacity()
        );
    }

    /**
     * Return a color with the same red, green and blue values as the given color, but with full opaqueness.
     */
    public static Color makeOpaque(Color color) {
        return Color.color(color.getRed(), color.getGreen(), color.getBlue());
    }
}
