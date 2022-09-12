package com.calabi.pixelator.view.palette;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.ui.control.ImageButton;
import com.calabi.pixelator.ui.window.Popup;
import com.calabi.pixelator.util.Do;

public class PaletteToggleButton extends ToggleButton {

    private final PaletteEditor editor;
    private final Label textPane;
    private final Popup popup;
    private EventHandler<? super MouseEvent> onClose;

    public PaletteToggleButton(Image image, PaletteEditor editor, String text, boolean closable) {
        getStyleClass().add("palette-toggle-button");
        setGraphic(new ImageView(image));

        this.editor = editor;

        GridPane content = new GridPane();
        content.setStyle("-fx-background-color: -px_empty_area");
        content.prefHeightProperty().bind(heightProperty());

        textPane = new Label(text);
        content.add(textPane, 0, 0);
        GridPane.setVgrow(textPane, Priority.SOMETIMES);
        GridPane.setMargin(textPane, new Insets(0, 6, 0, 5));

        Pane filler = new Pane();
        filler.setPrefWidth(7);
        hoverProperty().addListener((ov, o, n) -> {
            if (n && !isSelected()) {
                filler.setStyle("-fx-background-color: -px_hover");
            } else if (!n && !isSelected()) {
                filler.setStyle("-fx-background-color: transparent");
            } else if (isSelected()) {
                filler.setStyle("-fx-background-color: -px_selected");
            }
        });
        armedProperty().addListener((ov, o, n) -> Do.when(n, () -> filler.setStyle("-fx-background-color: -px_selected")));
        selectedProperty().addListener((ov, o, n) -> Do.when(n, () -> filler.setStyle("-fx-background-color: -px_selected")));
        content.add(filler, 2, 0);

        if (closable) {
            ImageButton close = new ImageButton(Images.CLOSE);
            close.getStyleClass().setAll("close-button");
            content.add(close, 1, 0);
            GridPane.setMargin(close, new Insets(0, 0, 0, 0));

            close.setOnMouseClicked(e -> close(e));
        }

        popup = new Popup(this, content, Direction.NORTH_WEST, Direction.NORTH_EAST, false);

        setOnMouseEntered(e -> popup.show(0, 0));
        setOnMouseExited(e -> maybeHidePopup(e));
        content.setOnMouseExited(e -> maybeHidePopup(e));

        setOnMouseClicked(e -> {
            if (MouseButton.PRIMARY.equals(e.getButton()) && e.getClickCount() == 2) {
                PaletteSelection.editPalette(getEditor());
                popup.hide();
            } else if (MouseButton.MIDDLE.equals(e.getButton())) {
                close(e);
            }
        });
    }

    public void setOnClose(EventHandler<? super MouseEvent> value) {
        onClose = value;
    }

    public void setPopupText(String text) {
        textPane.setText(text);
    }

    public PaletteEditor getEditor() {
        return editor;
    }

    private void close(MouseEvent e) {
        onClose.handle(e);
        popup.hide();
    }

    private void maybeHidePopup(MouseEvent e) { //FIXME: Coordinates are sometimes a tad wrong
        if (popup.isShowing()) {
            Bounds rightBounds = localToScreen(getBoundsInLocal());
            Bounds leftBounds = popup.getSkin().getNode().localToScreen(popup.getSkin().getNode().getBoundsInLocal());
            if (rightBounds == null
                    || e.getScreenX() < leftBounds.getMinX()
                    || e.getScreenX() > rightBounds.getMaxX()
                    || e.getScreenY() < leftBounds.getMinY()
                    || e.getScreenY() > rightBounds.getMaxY()) {
                popup.hide();
            }
        }
    }

}
