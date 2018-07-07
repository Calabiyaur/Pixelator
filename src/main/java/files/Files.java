package main.java.files;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;

import main.java.start.Main;
import main.java.util.FileUtil;
import org.apache.commons.lang3.tuple.Pair;

public class Files {

    private static Files instance = new Files();

    private Files() {
    }

    public static Files get() {
        return instance;
    }

    // -----------------------------------------  SAVE  -------------------------------------------

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
        Image image = pixelFile.getImage();
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            boolean successful = ImageIO.write(bImage, pixelFile.getExtension().name(), pixelFile.getFile());
            if (!successful) {
                throw new RuntimeException("Failed to save file " + pixelFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------  OPEN  -------------------------------------------

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

}
