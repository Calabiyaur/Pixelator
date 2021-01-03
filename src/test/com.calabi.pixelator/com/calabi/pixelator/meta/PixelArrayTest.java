package com.calabi.pixelator.meta;

import javafx.scene.paint.Color;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Assert.assertEquals(5, matrix.size());
        Assert.assertEquals(3, matrix.height());
    }

    @Test
    void testRemoveLines() {
        matrix.remove(2, 2);
        Assert.assertEquals(4, matrix.size());
        Assert.assertEquals(3, matrix.height());

        matrix.remove(0, 2);
        Assert.assertEquals(3, matrix.size());
        Assert.assertEquals(2, matrix.height());
    }

}
