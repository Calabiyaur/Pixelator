package main.java.view.palette;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

class PaletteTabButtons extends VBox {

    private final ToggleGroup tg = new ToggleGroup();
    private final IntegerProperty size = new SimpleIntegerProperty();

    public PaletteTabButtons() {
        setSpacing(1);

        tg.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null) {
                o.setSelected(true);
            }
        });

        tg.getToggles().addListener((ListChangeListener<Toggle>) c -> size.set(tg.getToggles().size()));
    }

    public PaletteToggleButton create(Image image, String text) {
        PaletteToggleButton button = new PaletteToggleButton(image, text);
        button.setOnMouseClicked(e -> {
            if (MouseButton.MIDDLE.equals(e.getButton())) {
                close(button);
            }
        });
        button.setToggleGroup(tg);
        getChildren().add(button);
        return button;
    }

    public PaletteToggleButton getSelected() {
        return (PaletteToggleButton) tg.getSelectedToggle();
    }

    public boolean isDefaultSelected() {
        return getChildren().indexOf(getSelected()) == 0;
    }

    public IntegerProperty sizeProperty() {
        return size;
    }

    public boolean closeSelected() {
        return close(getSelected());
    }

    private boolean close(PaletteToggleButton button) {
        int index = getChildren().indexOf(button);
        if (index == 0) {
            return false;
        }

        int newIndex = (index + 1) % getChildren().size();
        ((PaletteToggleButton) getChildren().get(newIndex)).fire();

        tg.getToggles().remove(button);
        return getChildren().remove(button);
    }
}
