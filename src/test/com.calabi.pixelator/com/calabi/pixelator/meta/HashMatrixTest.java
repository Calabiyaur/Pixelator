package com.calabi.pixelator.meta;

import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.view.palette.HashMatrix;

class HashMatrixTest {

    @Test
    void testConsistency() {
        HashMatrix<String> matrix = new HashMatrix<>();
        matrix.put(0, 0, "Zero");
        matrix.put(3, 5, "Right");
        matrix.put(1, 5, "Left");
        matrix.put(2, 7, "Bottom");

        Map<Integer, String> row = matrix.getRow(5);
        Map<Integer, String> column = matrix.getColumn(3);
        column.clear();

        Assert.assertEquals("Left", row.get(1));
        Assert.assertNull(row.get(3));
    }
}
