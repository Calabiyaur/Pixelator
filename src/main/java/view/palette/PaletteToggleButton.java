package main.java.view.palette;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;

import main.java.control.basic.ImageButton;
import main.java.res.Images;

public class PaletteToggleButton extends ToggleButton {

    private final Text textPane;
    private final PopupControl popup;
    private EventHandler<? super MouseEvent> onClose;

    public PaletteToggleButton(Image image, String text, boolean closable) {
        getStyleClass().add("palette-toggle-button");
        setGraphic(new ImageView(image));

        GridPane content = new GridPane();
        content.setStyle("-fx-background-color: #f4f4f4");
        textPane = new Text(text);
        ImageButton close = new ImageButton(Images.CLOSE_SMALL);
        close.getStyleClass().add("close-button");
        content.add(textPane, 0, 0);
        GridPane.setVgrow(textPane, Priority.SOMETIMES);
        if (closable) {
            content.add(close, 1, 0);
            GridPane.setMargin(close, new Insets(0, 7, 0, 0));
            GridPane.setMargin(textPane, new Insets(0, 6, 0, 5));
        } else {
            GridPane.setMargin(textPane, new Insets(0, 13, 0, 5));
        }
        content.prefHeightProperty().bind(heightProperty());

        popup = createPopup(content);

        setOnMouseEntered(e -> showPopup());
        setOnMouseExited(e -> maybeHidePopup(e));
        content.setOnMouseExited(e -> maybeHidePopup(e));

        close.setOnMouseClicked(e -> close(e));
        setOnMouseClicked(e -> {
            if (MouseButton.MIDDLE.equals(e.getButton())) {
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

    private void close(MouseEvent e) {
        onClose.handle(e);
        popup.hide();
    }

    private PopupControl createPopup(Node content) {
        PopupControl popup = new PopupControl() {
            {
                setSkin(new Skin<Skinnable>() {
                    @Override
                    public Skinnable getSkinnable() {
                        return PaletteToggleButton.this;
                    }

                    @Override
                    public Node getNode() {
                        return content;
                    }

                    @Override
                    public void dispose() {
                    }
                });
            }
        };
        popup.setAutoFix(true);
        popup.setConsumeAutoHidingEvents(false);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
        return popup;
    }

    private void showPopup() {
        if (!popup.isShowing()) {
            Bounds bounds = localToScreen(getBoundsInLocal());
            double x = bounds.getMinX();
            double y = bounds.getMinY();
            popup.show(getScene().getWindow(), x, y);
        }
    }

    private void maybeHidePopup(MouseEvent e) {
        if (popup.isShowing()) {
            Bounds rightBounds = localToScreen(getBoundsInLocal());
            Bounds leftBounds = popup.getSkin().getNode().localToScreen(popup.getSkin().getNode().getBoundsInLocal());
            if (e.getScreenX() < leftBounds.getMinX()
                    || e.getScreenX() > rightBounds.getMaxX()
                    || e.getScreenY() < leftBounds.getMinY()
                    || e.getScreenY() > rightBounds.getMaxY()) {
                popup.hide();
            }
        }
    }

}
