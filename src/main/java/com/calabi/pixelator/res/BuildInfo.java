package com.calabi.pixelator.res;

public final class BuildInfo {

    public static String getVersion() {
        String version = BuildInfo.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "LOCAL";
        }
        return version;
    }

    private BuildInfo() {
    }


}
