package com.calabi.pixelator.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;

class ShapeUtilTest {

    @Test
    void testGetLinePoints() {
        Point p1 = new Point(1, 3);
        Point p2 = new Point(2, 6);

        PointArray result1 = ShapeUtil.getLinePoints(p1, p2, 1);
        Assert.assertEquals(4, result1.size());

        PointArray result2 = ShapeUtil.getLinePoints(p2, p1, 1);
        Assert.assertEquals(4, result2.size());
    }

    @Test
    void testGetRectanglePoints() {
        Point p1 = new Point(3, 4);
        Point p2 = new Point(11, 2);
        Assert.assertEquals(27, ShapeUtil.getRectanglePoints(p1, p2, true).size());
    }

    @Test
    void testGetEllipsePoints() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(8, 2);
        Assert.assertEquals(16, ShapeUtil.getEllipsePoints(p1, p2, false, 1).size());
    }

    @Test
    void testGetFilledEllipsePoints() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(8, 2);
        Assert.assertEquals(23, ShapeUtil.getEllipsePoints(p1, p2, true, 1).size());
    }

}
