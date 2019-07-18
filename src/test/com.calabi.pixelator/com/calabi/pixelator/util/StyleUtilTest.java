package com.calabi.pixelator.util;

import javafx.scene.paint.Color;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class StyleUtilTest {

    @Test
    void testGetBackgroundColor() {
        String style = "-fx-background-color: #FF0000FF";
        Assert.assertEquals(Color.RED, StyleUtil.getBackgroundColor(style));
    }
}
