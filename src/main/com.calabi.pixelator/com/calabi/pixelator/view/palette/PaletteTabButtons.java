package com.calabi.pixelator.view.palette;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import com.calabi.pixelator.view.editor.ImageWindowContainer;

class PaletteTabButtons extends VBox {

    private final ToggleGroup tg = new ToggleGroup();
    private final IntegerProperty size = new SimpleIntegerProperty();
    private final ObjectProperty<PaletteToggleButton> selectedToggle = new SimpleObjectProperty<>();
    private PaletteToggleButton defaultToggle;

    public PaletteTabButtons() {
        setSpacing(1);

        tg.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null) {
                o.setSelected(true);
                selectedToggle.set((PaletteToggleButton) o);
            } else {
                selectedToggle.set((PaletteToggleButton) n);
            }
        });

        getChildren().addListener((ListChangeListener<Node>) c -> size.set(getChildren().size()));

        ImageWindowContainer.imageSelectedProperty().addListener((ov, o, n) -> {
            if (n) {
                if (!getChildren().contains(defaultToggle)) {
                    getChildren().add(0, defaultToggle);
                }
                if (!tg.getToggles().contains(defaultToggle)) {
                    Toggle selected = tg.getSelectedToggle();
                    tg.getToggles().add(0, defaultToggle);
                    tg.selectToggle(selected);
                }
            } else {
                closeHard(defaultToggle);
            }
        });
    }

    public PaletteToggleButton create(Image image, PaletteEditor editor, String text, boolean closable) {
        PaletteToggleButton button = new PaletteToggleButton(image, editor, text, closable);
        if (defaultToggle == null) {
            defaultToggle = button;
        }
        button.setOnClose(e -> close(button));
        button.setToggleGroup(tg);
        if (button != defaultToggle || ImageWindowContainer.imageSelectedProperty().get()) {
            getChildren().add(button);
        }
        return button;
    }

    public PaletteToggleButton getSelected() {
        return (PaletteToggleButton) tg.getSelectedToggle();
    }

    public ObjectProperty<PaletteToggleButton> selectedToggleProperty() {
        return selectedToggle;
    }

    public IntegerProperty sizeProperty() {
        return size;
    }

    public boolean closeSelected() {
        return close(getSelected());
    }

    private boolean close(PaletteToggleButton button) {
        if (button == defaultToggle) {
            return false;
        }

        return closeHard(button);
    }

    private boolean closeHard(PaletteToggleButton button) {
        int index = getChildren().indexOf(button);
        int newIndex = (index + 1) % getChildren().size();
        ((PaletteToggleButton) getChildren().get(newIndex)).fire();

        if (button != defaultToggle) {
            tg.getToggles().remove(button);
        }
        return getChildren().remove(button);
    }
}
