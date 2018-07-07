package main.java.util;

import main.java.standard.Point;
import main.java.standard.PointArray;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;

class ShapeUtilTest {

    @Test
    void testGetLinePoints() {
        Point p1 = new Point(1, 3);
        Point p2 = new Point(2, 6);

        PointArray result1 = ShapeUtil.getLinePoints(p1, p2);
        Assert.assertEquals(result1.size(), 4);
        //Assert.assertTrue(result1.contains(p1));
        //Assert.assertTrue(result1.contains(p2));

        PointArray result2 = ShapeUtil.getLinePoints(p2, p1);
        Assert.assertEquals(result2.size(), 4);
        //Assert.assertTrue(result2.contains(p1));
        //Assert.assertTrue(result2.contains(p2));
    }

    @Test
    void testGetRectanglePoints() {
        Point p1 = new Point(3, 4);
        Point p2 = new Point(11, 2);
        Assert.assertEquals(ShapeUtil.getRectanglePoints(p1, p2, true).size(), 27);
    }

}
