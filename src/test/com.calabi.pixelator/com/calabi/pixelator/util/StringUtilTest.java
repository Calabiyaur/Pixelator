package com.calabi.pixelator.util;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;

class StringUtilTest {

    @Test
    void toCamel() {
        Assert.assertEquals("camelHop", StringUtil.toCamel("CAMEL_HOP"));
    }

    @Test
    void toCamelCap() {
        Assert.assertEquals("CamelHop", StringUtil.toCamelCap("CAMEL_HOP"));
    }

}
