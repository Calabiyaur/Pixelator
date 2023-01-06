package com.calabi.pixelator.project;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.calabi.pixelator.config.ConfigObject;
import com.calabi.pixelator.file.Category;
import com.calabi.pixelator.file.Files;
import com.calabi.pixelator.file.PaletteFile;
import com.calabi.pixelator.file.PixelFile;

public class OpenedPalettesConfig extends ConfigObject {

    private final Set<PaletteFile> files = new HashSet<>();

    @Override
    public void build(String input) {
        for (String path : input.split(",")) {
            File file = new File(path.strip());
            if (file.exists()) {
                files.add((PaletteFile) Files.get().openFile(file, Category.PALETTE));
            }
        }
    }

    @Override
    public String toConfig() {
        return files.stream()
                .map(PixelFile::getFile)
                .filter(Objects::nonNull)
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(","));
    }

    public Set<PaletteFile> getFiles() {
        return files;
    }

}
