package com.calabi.pixelator.view.palette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.util.CollectionUtil;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.util.MapUtil;
import com.calabi.pixelator.view.palette.partition.HilbertPartition;
import com.calabi.pixelator.view.palette.partition.Mapping;
import com.calabi.pixelator.view.palette.partition.Partition;

public final class PaletteMaster {

    public static final int MAX_COLORS = Integer.MAX_VALUE;
    public static final int HUE_VARIETY = 8;

    public static Set<Color> extractColors(WritableImage image) {
        return ImageUtil.extractColors(image, MAX_COLORS);
    }

    public static WritableImage extractPalette(WritableImage image, int maxColors) {
        Set<Color> colors = ImageUtil.extractColors(image, maxColors);
        return extractPaletteHilbert(colors);
    }

    private static WritableImage extractPaletteHilbert(Set<Color> colors) {
        Mapping mapping = hilbertSort3d(colors);

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

}
