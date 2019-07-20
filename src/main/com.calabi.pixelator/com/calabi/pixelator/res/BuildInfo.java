package com.calabi.pixelator.res;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.calabi.pixelator.start.ExceptionHandler;

public final class BuildInfo {

    public static final String VERSION;

    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = BuildInfo.class.getResourceAsStream("/build-info.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (final IOException e) {
            ExceptionHandler.handle(e);
        }

        VERSION = properties.getProperty("pom.version");
    }

    private BuildInfo() {
    }


}
