package com.calabi.pixelator.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.meta.Point;

class CollectionUtilTest {

    @Test
    public void testGetExtrema() {
        List<Point> result = CollectionUtil.getExtrema(Arrays.asList(7, 13, 9, 8, 1, 10, 2, 5, 3));
        Assert.assertEquals(Arrays.asList(
                new Point(1, 3),
                new Point(5, 5),
                new Point(7, 10),
                new Point(13, 13)
        ), result);
    }

    @Test
    void testIntsBetween() {
        Assert.assertEquals(Arrays.asList(3, 4, 5, 6, 7), CollectionUtil.intsBetween(3, 7));
    }

}