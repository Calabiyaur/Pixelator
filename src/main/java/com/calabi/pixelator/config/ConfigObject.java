package com.calabi.pixelator.config;

public abstract class ConfigObject {

    public abstract void build(String input);

    public abstract String toConfig();

    @Override
    public String toString() {
        return toConfig();
    }
}
