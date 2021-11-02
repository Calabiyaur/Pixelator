package com.calabi.pixelator.view;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
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

        Assertions.assertEquals("Left", row.get(1));
        Assertions.assertNull(row.get(3));
    }
}
