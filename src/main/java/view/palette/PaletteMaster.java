package main.java.view.palette;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import main.java.view.ColorView;

public class PaletteMaster {

    public static void extractPalette(Image image) {
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

        ColorView.addPalette(palette);
    }

    private static Set<Color> extractColors(Image image) {
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

}
