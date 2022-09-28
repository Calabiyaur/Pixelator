package com.calabi.pixelator.meta;

import javafx.scene.paint.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.util.meta.PixelArray;

class PixelArrayTest {

    private PixelArray matrix;

    /**
     * Build up a matrix of the following structure:
     * x o x
     * o x o
     * x o x
     */
    @BeforeEach
    void setUp() {
        matrix = new PixelArray();
        matrix.add(0, 0, new PixelArray.Colors(null, Color.BLUE));
        matrix.add(2, 0, new PixelArray.Colors(null, Color.BLUE));
        matrix.add(1, 1, new PixelArray.Colors(null, Color.BLUE));
        matrix.add(0, 2, new PixelArray.Colors(null, Color.BLUE));
        matrix.add(2, 2, new PixelArray.Colors(null, Color.BLUE));
    }

    @Test
    void testSizes() {
        Assertions.assertEquals(5, matrix.size());
        Assertions.assertEquals(3, matrix.height());
    }

    @Test
    void testRemoveLines() {
        matrix.remove(2, 2);
        Assertions.assertEquals(4, matrix.size());
        Assertions.assertEquals(3, matrix.height());

        matrix.remove(0, 2);
        Assertions.assertEquals(3, matrix.size());
        Assertions.assertEquals(2, matrix.height());
    }

}
