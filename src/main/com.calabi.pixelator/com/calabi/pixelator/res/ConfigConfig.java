package com.calabi.pixelator.res;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import org.apache.commons.lang3.StringUtils;

public class ConfigConfig extends ConfigObject {

    private final static String SEPARATOR = ",";
    private final ObservableSet<Config> localConfigs = FXCollections.observableSet();

    public ConfigConfig() {
        localConfigs.addListener((InvalidationListener) observable -> {
            Config.CONFIG_CONFIG.putObject(this);
        });
    }

    @Override
    public void build(String input) {
        for (String s : input.split(SEPARATOR)) {
            localConfigs.add(Config.valueOf(s));
        }
    }

    @Override
    public String toConfig() {
        return StringUtils.join(localConfigs, SEPARATOR);
    }

    public ObservableSet<Config> getLocalConfigs() {
        return localConfigs;
    }

    public static ConfigConfig getDefault() {
        return new ConfigConfig();
    }

}
