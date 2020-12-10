package com.calabi.pixelator.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.util.shape.ShapeMaster;
import com.calabi.pixelator.view.ToolSettings;

class ShapeMasterTest {

    public static final ToolSettings DEFAULT =
            new ToolSettings(Integer.MAX_VALUE, Integer.MAX_VALUE, null, false, false, 1, 0, 0);
    public static final ToolSettings FILL =
            new ToolSettings(Integer.MAX_VALUE, Integer.MAX_VALUE, null, false, true, 1, 0, 0);

    @Test
    void testGetLinePoints() {
        Point p1 = new Point(1, 3);
        Point p2 = new Point(2, 6);

        PointArray result1 = ShapeMaster.getLinePoints(p1, p2, DEFAULT);
        Assert.assertEquals(4, result1.size());

        PointArray result2 = ShapeMaster.getLinePoints(p2, p1, DEFAULT);
        Assert.assertEquals(4, result2.size());
    }

    @Test
    void testGetEllipsePoints() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(8, 2);
        Assert.assertEquals(16, ShapeMaster.getEllipsePoints(p1, p2, DEFAULT).size());
    }

    @Test
    void testGetEllipsePointsOdd() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(5, 5);
        Assert.assertEquals(10, ShapeMaster.getEllipsePoints(p1, p2, DEFAULT).size());
    }

    @Test
    void testGetFilledEllipsePoints() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(8, 2);
        Assert.assertEquals(23, ShapeMaster.getEllipsePoints(p1, p2, FILL).size());
    }

}
