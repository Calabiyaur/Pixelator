package com.calabi.pixelator.util;

import javafx.scene.paint.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StyleUtilTest {

    @Test
    void testGetBackgroundColor() {
        String style = "-fx-background-color: #FF0000FF";
        Assertions.assertEquals(Color.RED, StyleUtil.getBackgroundColor(style));
    }
}
