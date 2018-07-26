package main.java.view.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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

    public static final int HUE_VARIETY = 8;

    public static WritableImage extractPalette(Image image) {
        int width = PaletteEditor.DEFAULT_WIDTH;
        List<List<Color>> colors = new ArrayList<>(extractAndSort(image, width));
        int height = colors.stream().max(Comparator.comparingInt(List::size)).get().size();

        WritableImage palette = new WritableImage(width, height);
        PixelWriter writer = palette.getPixelWriter();
        for (int i = 0; i < colors.size(); i++) {
            for (int j = 0; j < colors.get(i).size(); j++) {
                writer.setColor(i, j, colors.get(i).get(j));
            }
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
        return sort(colors);
    }

    public static List<List<Color>> extractAndSort(Image image, int columns) {
        Set<Color> colors = extractColors(image);
        return sort(colors, columns);
    }

    public static List<Color> sort(Set<Color> colors) {
        List<Color> result = sort(colors, HUE_VARIETY).stream().flatMap(List::stream).collect(Collectors.toList());
        if (result.size() != colors.size()) {
            throw new IllegalStateException("List size was reduced from " + colors.size() + " to " + result.size());
        }
        return result;
    }

    public static List<List<Color>> sort(Set<Color> colors, int columns) {
        return sort3(colors, columns);
    }

    /**
     * Sort the colors by hue, saturation and brightness.
     *
     * @return a list of lists, each list only containing colors of similar hue.
     */
    private static List<List<Color>> sort3(Set<Color> colors, int columns) {
        if (columns < 1) {
            throw new IllegalArgumentException("Columns must be at least 1");
        }
        List<Color> originalList = new ArrayList<>(colors);
        List<List<Color>> result = new ArrayList<>();
        double margin = 180. / columns;

        for (int i = 0; i < columns; i++) {
            final double hue = 360. * ((double) i / (double) columns);

            List<Color> column = originalList.stream().filter(
                    c -> Math.abs(c.getHue() - hue) <= margin
                    || Math.abs(c.getHue() - hue + 360) <= margin
                    || Math.abs(c.getHue() - hue - 360) <= margin
            ).collect(Collectors.toList());

            originalList.removeAll(column);
            result.add(sort2(column));
        }
        return result;
    }

    /**
     * Sort the colors by saturation and brightness.
     */
    private static List<Color> sort2(Collection<Color> colors) {
        double p = 0.05;
        return colors.stream().sorted((o1, o2) -> {
            if (o1 == null || o2 == null) {
                return 0;
            }

            return Double.compare(
                    o1.getSaturation() * p + ColorUtil.getLuminosity(o1) * (1 - p),
                    o2.getSaturation() * p + ColorUtil.getLuminosity(o2) * (1 - p)
            );

        }).collect(Collectors.toList());
    }

}
