package com.calabi.pixelator.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.calabi.pixelator.config.ConfigObject;

public class RecentProjectsConfig extends ConfigObject {

    private final List<File> files = new ArrayList<>();

    @Override
    public void build(String input) {
        for (String path : input.split(",")) {
            files.add(new File(path.strip()));
        }
    }

    @Override
    public String toConfig() {
        return files.stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(","));
    }

    public List<File> getFiles() {
        return files;
    }

}
