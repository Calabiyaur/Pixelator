package main.java.view.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PaletteMaster {

    public static WritableImage extractPalette(Image image) {
        List<Color> colors = new ArrayList<>(extractColors(image));
        int width = PaletteEditor.DEFAULT_WIDTH;
        int height = PaletteEditor.DEFAULT_HEIGHT;
        if (width * height < colors.size()) {
            height = (int) Math.ceil((double) colors.size() / (double) width);
        }

        WritableImage palette = new WritableImage(width, height);
        PixelWriter writer = palette.getPixelWriter();
        for (int i = 0; i < colors.size(); i++) {
            writer.setColor(i % width, i / width, colors.get(i));
        }

        return palette;
    }

    public static Set<Color> extractColors(Image image) {
        Set<Color> colors = new HashSet<>();
        PixelReader reader = image.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                colors.add(reader.getColor(i, j));
            }
        }
        colors.remove(Color.TRANSPARENT);
        return colors;
    }

    public static List<Color> sort(Collection<Color> colors) {
        return new ArrayList<>(colors); //TODO
    }

    public static List<Color> extractAndSort(Image image) {
        Set<Color> colors = extractColors(image);
        return sort(colors);
    }

}
