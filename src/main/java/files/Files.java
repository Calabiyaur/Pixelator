package main.java.files;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import main.java.files.io.BasicImageWriter;
import main.java.files.io.PIXImageWriter;
import main.java.files.io.PixelFileWriter;
import main.java.start.Main;
import main.java.util.FileUtil;
import org.apache.commons.lang3.tuple.Pair;

public class Files {

    private static Files instance = new Files();
    private static Map<Extension, PixelFileWriter> writers = new HashMap<>();
    static {
        writers.put(null, new BasicImageWriter());
        writers.put(Extension.PIX, new PIXImageWriter());
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
        files.forEach(file -> {
            Pair<File, Image> pair = openFile(file);
            if (pair != null) {
                result.add(FileUtil.createFile(category, pair.getRight(), pair.getLeft()));
            }
        });

        return result;
    }

    private Pair<File, Image> openFile(File file) {
        try {
            Image image = new Image(file.toURI().toURL().toString());
            return Pair.of(file, image);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage() + "\nMalformed URL: " + file.getPath());
        }
        return null;
    }

    public List<PixelFile> openByName(Collection<String> files) {
        List<PixelFile> result = new ArrayList<>();
        for (String path : files) {
            File file = new File(path);
            Pair<File, Image> pair = openFile(file);
            if (pair != null) {
                PixelFile pixelFile = FileUtil.createFile(pair.getRight(), pair.getLeft());
                result.add(pixelFile);
            }
        }
        return result;
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

}
