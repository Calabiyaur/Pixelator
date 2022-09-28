package com.calabi.pixelator.util;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.util.meta.Point;

class CollectionUtilTest {

    @Test
    public void testGetExtrema() {
        List<Point> result = CollectionUtil.getExtrema(Arrays.asList(7, 13, 9, 8, 1, 10, 2, 5, 3));
        Assertions.assertEquals(Arrays.asList(
                new Point(1, 3),
                new Point(5, 5),
                new Point(7, 10),
                new Point(13, 13)
        ), result);
    }

    @Test
    void testIntsBetween() {
        Assertions.assertEquals(Arrays.asList(3, 4, 5, 6, 7), CollectionUtil.intsBetween(3, 7));
    }

    @Test
    void testReduceEvenly() {
        List<String> list = Arrays.asList("A", "1", "2", "B", "3", "4", "C");
        List<String> reduced = Arrays.asList("A", "B", "C");
        Assertions.assertEquals(reduced, CollectionUtil.reduceEvenly(list, 3));
    }

}
