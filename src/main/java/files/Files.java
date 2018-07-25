package main.java.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.stage.FileChooser;

import main.java.files.io.BasicImageReader;
import main.java.files.io.BasicImageWriter;
import main.java.files.io.PIXImageReader;
import main.java.files.io.PIXImageWriter;
import main.java.files.io.PixelFileReader;
import main.java.files.io.PixelFileWriter;
import main.java.start.Main;
import main.java.util.FileUtil;

public class Files {

    private static Files instance = new Files();
    private static Map<Extension, PixelFileWriter> writers = new HashMap<>();
    private static Map<Extension, PixelFileReader> readers = new HashMap<>();

    static {
        writers.put(null, new BasicImageWriter());
        PIXImageWriter writer = new PIXImageWriter();
        writers.put(Extension.PIX, writer);
        writers.put(Extension.PAL, writer);
        writers.put(Extension.PALI, writer);
    }

    static {
        readers.put(null, new BasicImageReader());
        PIXImageReader reader = new PIXImageReader();
        readers.put(Extension.PIX, reader);
        readers.put(Extension.PAL, reader);
        readers.put(Extension.PALI, reader);
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
        File file = dialog.showSaveDialog(Main.getPrimaryStage());
        if (file == null) {
            return false;
        }

        pixelFile.setFile(file);
        updateDirectory(pixelFile.getCategory(), file);

        return true;
    }

    private void saveFile(PixelFile pixelFile) {
        if (pixelFile.getFile() == null) {
            throw new NullPointerException();
        }

        PixelFileWriter writer = getWriter(pixelFile.getExtension());
        try {
            writer.write(pixelFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file " + pixelFile, e);
        }
    }

    public void saveConfig(PixelFile pixelFile) {
        PixelFileWriter writer = getWriter(pixelFile.getExtension());
        try {
            writer.writeConfig(pixelFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config " + pixelFile, e);
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
        List<File> files = dialog.showOpenMultipleDialog(Main.getPrimaryStage());
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
            PixelFileBuilder builder = reader.read(file);
            builder.setCategory(category);
            return builder.build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file " + file, e);
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

    public PaletteFile openSingleImage() {
        return (PaletteFile) openSingle(Category.IMAGE);
    }

    public PaletteFile openSinglePalette() {
        return (PaletteFile) openSingle(Category.PALETTE);
    }

    private PixelFile openSingle(Category category) {
        File fileDirectory = category.getDirectory();
        FileChooser dialog = new FileChooser();
        dialog.setInitialDirectory(fileDirectory);
        dialog.getExtensionFilters().addAll(category.getExtensionFiltersToOpen());
        File file = dialog.showOpenDialog(Main.getPrimaryStage());
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
