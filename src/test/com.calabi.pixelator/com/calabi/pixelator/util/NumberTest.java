package com.calabi.pixelator.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class NumberTest {

    @Test
    void round() {
        Assert.assertEquals(Float.toString(4.5f), Float.toString(9 / 2f));
    }
}
