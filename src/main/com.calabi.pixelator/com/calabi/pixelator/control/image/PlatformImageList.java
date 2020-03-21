package com.calabi.pixelator.control.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        reload(image.getFrameList());

        image.getFrameList().addListener((ListChangeListener<PlatformImage>) c -> reload(c.getList()));
    }

    public void reload(List<? extends PlatformImage> c) {
        this.clear();
        this.addPlatformImages(c);
    }

    private void addPlatformImages(Collection<? extends PlatformImage> images) {
        for (PlatformImage platformImage : images) {
            this.add(createSingleImage(platformImage));
        }
    }

    private Image createSingleImage(PlatformImage platformImage) {
        return ReflectionUtil.invokeMethod(this.image, "fromPlatformImage", platformImage);
    }

}
