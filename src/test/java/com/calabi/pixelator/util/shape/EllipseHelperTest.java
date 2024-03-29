package com.calabi.pixelator.util.shape;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.util.meta.PointArray;

class EllipseHelperTest {

    @Test
    void testGetEllipse3by3() {
        PointArray expected = new PointArray();
        expected.add(1, 0);
        expected.add(2, 1);
        expected.add(1, 2);
        expected.add(0, 1);
        expected.add(1, 1);

        PointArray ellipse = EllipseHelper.getEllipse(1, 1, 1, 1, 1, 1, true, Integer.MAX_VALUE, Integer.MAX_VALUE);

        Assertions.assertEquals(expected, ellipse, String.format("expected #= %s, ellipse = %s", expected, ellipse));
    }

    @Test
    void testGetEllipse2by2() {
        PointArray expected = new PointArray();
        expected.add(0, 0);
        expected.add(1, 0);
        expected.add(0, 1);
        expected.add(1, 1);

        PointArray ellipse = EllipseHelper.getEllipse(1, 1, 1, 1, 2, 2, true, Integer.MAX_VALUE, Integer.MAX_VALUE);

        Assertions.assertEquals(expected, ellipse, String.format("expected = %s, ellipse = %s", expected, ellipse));
    }

}
