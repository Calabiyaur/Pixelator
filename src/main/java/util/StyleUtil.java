package main.java.util;

import javafx.scene.paint.Color;

import org.apache.commons.lang3.StringUtils;

public class StyleUtil {

    public static Color getBackgroundColor(String style) {
        String s = StringUtils.substringAfter(style, "-fx-background-color:");

        String hexCode = "";
        for (char c : s.replaceAll("[# ]", "").toCharArray()) {
            if (Character.isDigit(c) || Character.isLetter(c)) {
                hexCode += c;
            } else {
                break;
            }
        }

        return ColorUtil.valueOf(hexCode);
    }
}
