package com.calabi.pixelator.ui.control.number;

public enum IntPattern {

    POSITIVE("[0-9]*"),
    INTEGER("[-0-9]*");

    private final String regex;

    IntPattern(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

}
