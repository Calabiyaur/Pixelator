package com.calabi.pixelator.control.image;

import java.util.ArrayList;
import java.util.Collection;

import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;

import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.tk.PlatformImage;

import com.calabi.pixelator.util.ReflectionUtil;

public class PlatformImageList extends ObservableListWrapper<Image> {

    private Image image;

    public PlatformImageList(WritableImage image) {
        super(new ArrayList<>());

        this.image = image;

        addPlatformImages(image.getFrameList());

        image.getFrameList().addListener((ListChangeListener<PlatformImage>) c -> {
            while (c.next()) {
                removePlatformImages(c.getRemoved());
                addPlatformImages(c.getAddedSubList());
            }
        });
    }

    private void addPlatformImages(Collection<? extends PlatformImage> images) {
        for (PlatformImage platformImage : images) {
            this.add(createSingleImage(platformImage));
        }
    }

    private void removePlatformImages(Collection<? extends PlatformImage> images) {
        for (PlatformImage platformImage : images) {
            this.removeIf(i -> ReflectionUtil.invokeMethod(i, "getPlatformImage") == platformImage);
        }
    }

    private Image createSingleImage(PlatformImage platformImage) {
        return ReflectionUtil.invokeMethod(this.image, "fromPlatformImage", platformImage);
    }

}
