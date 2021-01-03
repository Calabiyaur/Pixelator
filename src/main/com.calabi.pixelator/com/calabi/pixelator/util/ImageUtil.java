package com.calabi.pixelator.util;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import com.sun.javafx.tk.PlatformImage;

import com.calabi.pixelator.meta.Pixel;
import com.calabi.pixelator.meta.PixelArray;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.view.palette.SortMaster;

public class ImageUtil {

    public static Image get(PixelArray pixels) {
        int x1 = Integer.MAX_VALUE;
        int y1 = Integer.MAX_VALUE;
        int x2 = 0;
        int y2 = 0;
        for (Pixel pixel : pixels.getPoints()) {
            x1 = Math.min(pixel.getX(), x1);
            y1 = Math.min(pixel.getY(), y1);
            x2 = Math.max(pixel.getX(), x2);
            y2 = Math.max(pixel.getY(), y2);
        }
        if (x1 >= x2 || y1 >= y2) {
            throw new IllegalStateException("Failed converting pixels to Image");
        }
        WritableImage image = new WritableImage(x2 - x1 + 1, y2 - y1 + 1);
        PixelWriter writer = image.getPixelWriter();
        for (Pixel pixel : pixels.getPoints()) {
            writer.setColor(pixel.getX() - x1, pixel.getY() - y1, pixel.getColor());
        }
        return image;
    }

    public static boolean outOfBounds(Image image, int x, int y) {
        return x < 0 || x >= (int) image.getWidth() || y < 0 || y >= (int) image.getHeight();
    }

    public static boolean outOfBounds(Image image, Point point) {
        return outOfBounds(image, point.getX(), point.getY());
    }

    public static int countColors(WritableImage image) {
        Set<Color> colors = extractColors(image, Integer.MAX_VALUE);
        return colors.size();
    }

    public static Set<Color> extractColors(WritableImage image, Integer maxColors) {
        Set<Color> colors = new HashSet<>();

        if (image.isAnimated()) {
            for (PlatformImage frame : image.getFrames()) {
                for (int i = 0; i < image.getWidth(); i++) {
                    for (int j = 0; j < image.getHeight(); j++) {
                        colors.add(getColor(frame, i, j));
                    }
                }
            }
        } else {
            PixelReader reader = image.getPixelReader();
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    colors.add(reader.getColor(i, j));
                }
            }
        }
        colors.remove(Color.TRANSPARENT);

        if (colors.size() <= maxColors) {
            return colors;
        } else {
            return new HashSet<>(CollectionUtil.reduceEvenly(SortMaster.sortByValues(colors), maxColors));
        }
    }

    public static Color getColor(PlatformImage platformImage, int x, int y) {
        com.sun.prism.Image prismImage = (com.sun.prism.Image) platformImage;

        int argb = prismImage.getArgb(x, y);
        double a = (0xff & (argb >> 24)) / 255d;
        double r = (0xff & (argb >> 16)) / 255d;
        double g = (0xff & (argb >> 8)) / 255d;
        double b = (0xff & (argb)) / 255d;

        return Color.color(r, g, b, a);
    }

}
