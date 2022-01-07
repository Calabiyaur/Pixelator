package com.calabi.pixelator.ui.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;

import com.sun.javafx.tk.PlatformImage;

import com.calabi.pixelator.util.ReflectionUtil;

public class PlatformImageList extends ArrayList<Image> {

    private final Image image;
    private final List<Runnable> listeners = new ArrayList<>();

    public PlatformImageList(WritableImage image) {
        this.image = image;

        reload(image.getFrameList());

        image.getFrameList().addListener((ListChangeListener<PlatformImage>) c -> reload(c.getList()));
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void reload(List<? extends PlatformImage> c) {
        this.clear();
        this.addPlatformImages(c);

        fireChange();
    }

    private void addPlatformImages(Collection<? extends PlatformImage> images) {
        for (PlatformImage platformImage : images) {
            this.add(createSingleImage(platformImage));
        }
    }

    private Image createSingleImage(PlatformImage platformImage) {
        return ReflectionUtil.invokeMethod(this.image, "fromPlatformImage", platformImage);
    }

    private void fireChange() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

}
