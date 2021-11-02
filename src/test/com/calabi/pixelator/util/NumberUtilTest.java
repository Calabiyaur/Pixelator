package com.calabi.pixelator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberUtilTest {

    @Test
    public void testRound() {
        Assertions.assertEquals(17.5, NumberUtil.round(17.4713, 1), 0);
        Assertions.assertEquals(6., NumberUtil.round(6.1111, 0), 0);
        Assertions.assertEquals(.25, NumberUtil.round(0.2499999999999999999, 2), 0);
    }

    @Test
    public void testMinMax() {
        Assertions.assertEquals(5., NumberUtil.minMax(4., 5., 6.), 0);
        Assertions.assertEquals(6., NumberUtil.minMax(4., 7., 6.), 0);
        Assertions.assertEquals(4., NumberUtil.minMax(4., 3., 6.), 0);
    }

}
