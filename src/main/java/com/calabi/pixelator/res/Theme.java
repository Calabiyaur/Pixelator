package com.calabi.pixelator.res;

public enum Theme {

    BRIGHT("/style/bright-theme.css"),
    DARK("/style/dark-theme.css");

    private final String path;

    Theme(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
