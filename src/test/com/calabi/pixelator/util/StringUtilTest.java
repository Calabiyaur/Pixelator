package com.calabi.pixelator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilTest {

    @Test
    void toCamel() {
        Assertions.assertEquals("camelHop", StringUtil.toCamel("CAMEL_HOP"));
    }

    @Test
    void toCamelCap() {
        Assertions.assertEquals("CamelHop", StringUtil.toCamelCap("CAMEL_HOP"));
    }

    @Test
    void toCaps() {
        Assertions.assertEquals("CAMEL_HOP", StringUtil.toCaps("camelHop"));
    }

}
