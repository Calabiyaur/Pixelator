package main.java.view.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import main.java.util.ColorUtil;

public class PaletteMaster {

    public static WritableImage extractPalette(Image image) {
        List<Color> colors = new ArrayList<>(extractAndSort(image));
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

    public static List<Color> extractAndSort(Image image) {
        Set<Color> colors = extractColors(image);
        return sort(colors, 1);
    }

    public static List<Color> sort(Collection<Color> colors, int rep) {
        return colors.stream().sorted((o1, o2) -> {
            if (o1 == null || o2 == null) {
                return 0;
            }

            int h1 = (int) (o1.getHue() * rep);
            int h2 = (int) (o2.getHue() * rep);
            int level1 = Integer.compare(h1, h2);
            if (level1 != 0) {
                return level1;
            }

            int lum1 = (int) (ColorUtil.getLuminosity(o1) * rep);
            int lum2 = (int) (ColorUtil.getLuminosity(o2) * rep);
            int level2 = Integer.compare(lum1, lum2);
            if (level2 != 0) {
                return level2;
            }

            //return Integer.compare(v1, v2);
            return Double.compare(o1.getBrightness(), o2.getBrightness());

        }).collect(Collectors.toList());
    }

}
