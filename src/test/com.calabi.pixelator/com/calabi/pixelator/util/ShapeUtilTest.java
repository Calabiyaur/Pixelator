package com.calabi.pixelator.util;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;

class ShapeUtilTest {

    @Test
    void testGetLinePoints() {
        Point p1 = new Point(1, 3);
        Point p2 = new Point(2, 6);

        PointArray result1 = ShapeUtil.getLinePoints(p1, p2, 1);
        Assert.assertEquals(result1.size(), 4);

        PointArray result2 = ShapeUtil.getLinePoints(p2, p1, 1);
        Assert.assertEquals(result2.size(), 4);
    }

    @Test
    void testGetRectanglePoints() {
        Point p1 = new Point(3, 4);
        Point p2 = new Point(11, 2);
        Assert.assertEquals(ShapeUtil.getRectanglePoints(p1, p2, true).size(), 27);
    }

    @Test
    void testGetEllipsePoints() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(8, 2);
        ShapeUtil.getEllipsePoints(p1, p2, false, 1);
    }

}
