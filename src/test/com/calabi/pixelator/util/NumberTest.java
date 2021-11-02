package com.calabi.pixelator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberTest {

    @Test
    void round() {
        Assertions.assertEquals(Float.toString(4.5f), Float.toString(9 / 2f));
    }
}
