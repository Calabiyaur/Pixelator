package com.calabi.pixelator.res;

public final class BuildInfo {

    public static final String VERSION;

    static {
        VERSION = BuildInfo.class.getPackage().getImplementationVersion();
    }

    private BuildInfo() {
    }


}
