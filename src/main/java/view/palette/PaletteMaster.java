package main.java.view.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import main.java.meta.HashMatrix;
import main.java.meta.Matrix;
import main.java.meta.Point;
import main.java.util.CollectionUtil;
import main.java.util.ColorUtil;
import main.java.util.MapUtil;
import main.java.view.palette.partition.HilbertPartition;
import main.java.view.palette.partition.Mapping;
import main.java.view.palette.partition.Partition;

public class PaletteMaster {

    public static final int HUE_VARIETY = 8;

    public static WritableImage extractPalette(Image image) {
        Mapping mapping = hilbertSort3d(extractColors(image));

        int width = Math.max(1, mapping.width());
        int height = Math.max(1, mapping.height());
        WritableImage palette = new WritableImage(width, height);
        PixelWriter writer = palette.getPixelWriter();
        for (Map.Entry<Color, Point> entry : mapping.get().entrySet()) {
            int x = entry.getValue().getX();
            int y = entry.getValue().getY();
            writer.setColor(x, y, entry.getKey());
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

    public static List<Color> sort(Set<Color> colors) {
        List<Color> result = sortInternal(colors).stream().flatMap(List::stream).collect(Collectors.toList());
        if (result.size() != colors.size()) {
            throw new IllegalStateException("List size was reduced from " + colors.size() + " to " + result.size());
        }
        return result;
    }

    private static List<List<Color>> sortInternal(Set<Color> colors) {
        return sort3(colors);
    }

    /**
     * Sort the colors by using a 2-dimensional hilbert curve through the 3-dimensional color-space,
     * thereby neglecting opacity.
     */
    private static Mapping hilbertSort3d(Set<Color> colors) {
        Partition partition = new HilbertPartition();
        for (Color color : colors) {
            partition.add(color);
        }
        Mapping mapping = partition.createMapping();
        Matrix<Color> colorMatrix = new HashMatrix<>();
        for (Map.Entry<Color, Point> entry : mapping.get().entrySet()) {
            int x = entry.getValue().getX();
            int y = entry.getValue().getY();
            colorMatrix.put(x, y, entry.getKey());
        }

        List<Integer> unfinishedRows = CollectionUtil.intsBetween(0, mapping.height() - 1);
        List<Integer> unfinishedColumns = CollectionUtil.intsBetween(0, mapping.width() - 1);
        List<Integer> retainedRows = new ArrayList<>(unfinishedRows);
        List<Integer> retainedColumns = new ArrayList<>(unfinishedColumns);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0, j = 0; i < unfinishedRows.size() || j < unfinishedColumns.size(); i += 2, j += 2) {
                if (!unfinishedRows.isEmpty() && i < unfinishedRows.size()) {
                    Integer rowNum = unfinishedRows.get(i);
                    if (i + 1 >= unfinishedRows.size()) {
                        unfinishedRows.remove(rowNum);
                        changed = true;
                    } else {
                        Integer nextRowNum = unfinishedRows.get(i + 1);

                        Map<Integer, Color> row = colorMatrix.getRow(rowNum);
                        Map<Integer, Color> nextRow = colorMatrix.getRow(nextRowNum);
                        boolean remove = true;
                        if (row != null) {
                            if (nextRow == null || !MapUtil.getAll(nextRow, row.keySet()).isEmpty()) {
                                remove = false;
                                i--;
                            } else {
                                for (Map.Entry<Integer, Color> entry : row.entrySet()) {
                                    nextRow.put(entry.getKey(), entry.getValue());
                                }
                                row.clear();
                            }
                        }
                        if (remove) {
                            unfinishedRows.remove(rowNum);
                            retainedRows.remove(rowNum);
                            changed = true;
                        }
                    }
                }
                if (!unfinishedColumns.isEmpty() && j < unfinishedColumns.size()) {
                    Integer colNum = unfinishedColumns.get(j);
                    if (j + 1 >= unfinishedColumns.size()) {
                        unfinishedColumns.remove(colNum);
                        changed = true;
                    } else {
                        Integer nextColNum = unfinishedColumns.get(j + 1);

                        Map<Integer, Color> column = colorMatrix.getColumn(colNum);
                        Map<Integer, Color> nextColumn = colorMatrix.getColumn(nextColNum);
                        boolean remove = true;
                        if (column != null) {
                            if (nextColumn == null || !MapUtil.getAll(nextColumn, column.keySet()).isEmpty()) {
                                remove = false;
                                j--;
                            } else {
                                for (Map.Entry<Integer, Color> entry : column.entrySet()) {
                                    nextColumn.put(entry.getKey(), entry.getValue());
                                }
                                column.clear();
                            }
                        }
                        if (remove) {
                            unfinishedColumns.remove(colNum);
                            retainedColumns.remove(colNum);
                            changed = true;
                        }
                    }
                }
            }
        }

        Map<Color, Point> result = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Color>> rowEntry : colorMatrix.rows()) {
            int y = retainedRows.indexOf(rowEntry.getKey());
            for (Map.Entry<Integer, Color> entry : rowEntry.getValue().entrySet()) {
                int x = retainedColumns.indexOf(entry.getKey());
                result.put(entry.getValue(), new Point(x, y));
            }
        }
        return new Mapping(retainedColumns.size(), retainedRows.size(), result);
    }

    /**
     * Sort the colors by hue, saturation and brightness.
     *
     * @return a list of lists, each list only containing colors of similar hue.
     */
    private static List<List<Color>> sort3(Set<Color> colors) {
        if (HUE_VARIETY < 1) {
            throw new IllegalArgumentException("Columns must be at least 1");
        }
        List<Color> originalList = new ArrayList<>(colors);
        List<List<Color>> result = new ArrayList<>();
        double margin = 180. / HUE_VARIETY;

        for (int i = 0; i < HUE_VARIETY; i++) {
            final double hue = 360. * ((double) i / (double) HUE_VARIETY);

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
