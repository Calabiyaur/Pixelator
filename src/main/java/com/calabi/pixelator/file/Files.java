package com.calabi.pixelator.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.stage.FileChooser;

import com.calabi.pixelator.file.io.PixelFileReader;
import com.calabi.pixelator.file.io.PixelFileWriter;
import com.calabi.pixelator.main.Pixelator;
import com.calabi.pixelator.util.FileUtil;

public class Files {

    private static final Files instance = new Files();
    private static final Map<Extension, PixelFileWriter> writers = new HashMap<>();
    private static final Map<Extension, PixelFileReader> readers = new HashMap<>();

    static {
        writers.put(null, new PixelFileWriter()); //TODO: Create writers for PNG, JPG, ...
        readers.put(null, new PixelFileReader()); //TODO: Create readers for PNG, JPG, ...
    }

    private Files() {
    }

    public static Files get() {
        return instance;
    }

    public void save(PixelFile pixelFile) {
        if (!pixelFile.isNew() || createFile(pixelFile)) {
            saveFile(pixelFile);
        }
    }

    public void create(PixelFile pixelFile) {
        if (createFile(pixelFile)) {
            saveFile(pixelFile);
        }
    }

    public boolean createFile(PixelFile pixelFile) {
        FileChooser dialog = new FileChooser();
        dialog.setInitialDirectory(pixelFile.getCategory().getDirectory());
        dialog.setInitialFileName(pixelFile.getName());
        List<FileChooser.ExtensionFilter> filters = pixelFile.getCategory().getExtensionFiltersToSave();
        dialog.getExtensionFilters().addAll(filters);
        dialog.setSelectedExtensionFilter(pixelFile.getUsedFilter(filters));
        File file = dialog.showSaveDialog(Pixelator.getPrimaryStage());
        if (file == null) {
            return false;
        }

        pixelFile.setFile(file);
        updateDirectory(pixelFile.getCategory(), file);

        return true;
    }

    public void saveFile(PixelFile pixelFile) {
        if (pixelFile.getFile() == null) {
            throw new NullPointerException();
        }

        PixelFileWriter writer = getWriter(pixelFile.getExtension());
        try {
            writer.write(pixelFile);
        } catch (IOException e) {
            throw new FileException("Failed to save file " + pixelFile, e);
        }
    }

    public void saveConfig(PixelFile pixelFile) {
        PixelFileWriter writer = getWriter(pixelFile.getExtension());
        try {
            writer.saveConfig(pixelFile);
        } catch (IOException e) {
            throw new FileException("Failed to save config " + pixelFile, e);
        }
    }

    public void savePreview(PaletteFile paletteFile) {
        PixelFileWriter writer = getWriter(paletteFile.getExtension());
        try {
            writer.writePreview(paletteFile);
        } catch (IOException e) {
            throw new FileException("Failed to save preview " + paletteFile, e);
        }
    }

    public List<ImageFile> openImages() {
        return open(Category.IMAGE).stream().map(f -> (ImageFile) f).collect(Collectors.toList());
    }

    public List<PaletteFile> openPalettes() {
        return open(Category.PALETTE).stream().map(f -> (PaletteFile) f).collect(Collectors.toList());
    }

    private List<PixelFile> open(Category category) {
        List<PixelFile> result = new ArrayList<>();

        File fileDirectory = category.getDirectory();
        FileChooser dialog = new FileChooser();
        dialog.setInitialDirectory(fileDirectory);
        dialog.getExtensionFilters().addAll(category.getExtensionFiltersToOpen());
        List<File> files = dialog.showOpenMultipleDialog(Pixelator.getPrimaryStage());
        if (files == null || files.isEmpty()) {
            return result;
        }

        updateDirectory(category, files.get(0));
        files.forEach(file -> result.add(openFile(file, category)));

        return result;
    }

    private PixelFile openFile(File file, Category category) {
        PixelFileReader reader = getReader(FileUtil.getExtension(file));
        try {
            return reader.read(file)
                    .category(category)
                    .build();
        } catch (IOException e) {
            throw new FileException("Failed to read file " + file, e);
        }
    }

    public List<PixelFile> openByName(Collection<String> files) {
        List<PixelFile> result = new ArrayList<>();
        for (String path : files) {
            File file = new File(path);
            result.add(openFile(file, Category.IMAGE));
        }
        return result;
    }

    public ImageFile openSingleImage() {
        return (ImageFile) openSingle(Category.IMAGE);
    }

    public PaletteFile openSinglePalette() {
        return (PaletteFile) openSingle(Category.PALETTE);
    }

    private PixelFile openSingle(Category category) {
        File fileDirectory = category.getDirectory();
        FileChooser dialog = new FileChooser();
        dialog.setInitialDirectory(fileDirectory);
        dialog.getExtensionFilters().addAll(category.getExtensionFiltersToOpen());
        File file = dialog.showOpenDialog(Pixelator.getPrimaryStage());
        if (file == null) {
            return null;
        }

        return openFile(file, category);
    }

    private void updateDirectory(Category category, File directory) {
        category.setDirectory(directory.getParent());
    }

    private PixelFileWriter getWriter(Extension extension) {
        PixelFileWriter writer = writers.get(extension);
        if (writer == null) {
            return writers.get(null);
        } else {
            return writer;
        }
    }

    private PixelFileReader getReader(Extension extension) {
        PixelFileReader reader = readers.get(extension);
        if (reader == null) {
            return readers.get(null);
        } else {
            return reader;
        }
    }

}
