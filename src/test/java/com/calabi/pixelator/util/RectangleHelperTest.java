package com.calabi.pixelator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.util.shape.RectangleHelper;

class RectangleHelperTest {

    @Test
    void testGetRectanglePoints() {
        Point p1 = new Point(3, 4);
        Point p2 = new Point(11, 2);
        Assertions.assertEquals(27, RectangleHelper.getRectanglePoints(p1, p2, true, Integer.MAX_VALUE, Integer.MAX_VALUE).size());
    }

}
