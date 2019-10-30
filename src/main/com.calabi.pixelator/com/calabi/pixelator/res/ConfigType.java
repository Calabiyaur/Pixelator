package com.calabi.pixelator.res;

enum ConfigType {

    INT,
    DOUBLE,
    BOOLEAN,
    STRING,
    OBJECT;

    public String toString() {
        return name();
    }

}
