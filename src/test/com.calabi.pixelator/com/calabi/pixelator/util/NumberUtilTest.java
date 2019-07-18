package com.calabi.pixelator.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class NumberUtilTest {

    @Test
    public void testRound() {
        Assert.assertEquals(17.5, NumberUtil.round(17.4713, 1), 0);
        Assert.assertEquals(6., NumberUtil.round(6.1111, 0), 0);
        Assert.assertEquals(.25, NumberUtil.round(0.2499999999999999999, 2), 0);
    }

    @Test
    public void testMinMax() {
        Assert.assertEquals(5., NumberUtil.minMax(4., 5., 6.), 0);
        Assert.assertEquals(6., NumberUtil.minMax(4., 7., 6.), 0);
        Assert.assertEquals(4., NumberUtil.minMax(4., 3., 6.), 0);
    }

}
