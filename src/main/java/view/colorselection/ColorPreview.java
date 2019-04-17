package main.java.view.colorselection;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import main.java.res.Images;
import main.java.util.BackgroundUtil;
import main.java.util.ColorUtil;
import main.java.view.ColorView;
import main.java.view.tool.Pick;

class ColorPreview extends StackPane {

    private static final int MAX_RECENT_COLORS = 100;

    private final VBox colorPane;
    private final TextField hexField;
    private final FlowPane recentColorPane;
    private final ObservableSet<Color> recentColors = FXCollections.observableSet();
    private final Map<Color, Node> recentColorMap = new HashMap<>();

    public ColorPreview() {
        hexField = new TextField("0x000000FF");
        Pane filler = new Pane();
        VBox.setVgrow(filler, Priority.ALWAYS);
        recentColorPane = new FlowPane();
        colorPane = new VBox(hexField, filler, recentColorPane);
        hexField.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        setBackground(BackgroundUtil.repeat(Images.CHECKERS));

        colorPane.backgroundProperty().addListener((ov, o, n) -> {
            Color backGround = (Color) n.getFills().get(0).getFill();
            Color textColor = ColorUtil.getHighContrast(backGround);
            hexField.setStyle("-fx-text-fill: " + ColorUtil.toString(textColor));
        });

        recentColors.addListener((SetChangeListener<Color>) c -> {
            if (c.wasRemoved()) {
                recentColorPane.getChildren().remove(recentColorMap.remove(c.getElementRemoved()));
            } else {
                recentColorPane.getChildren().remove(recentColorMap.remove(c.getElementAdded()));
                Color color = c.getElementAdded();
                Rectangle colorPane = createRecentColorButton(color);
                recentColorMap.put(color, colorPane);
                recentColorPane.getChildren().add(0, colorPane);
                if (recentColorPane.getChildren().size() > MAX_RECENT_COLORS) {
                    recentColorPane.getChildren().subList(MAX_RECENT_COLORS, recentColorPane.getChildren().size()).clear();
                }
            }
        });

        getChildren().addAll(colorPane);
    }

    private Rectangle createRecentColorButton(Color color) {
        Rectangle colorButton = new Rectangle(10, 10);
        colorButton.setFill(color);
        colorButton.setOnMousePressed(e -> ColorView.setColor(color));
        colorButton.setOnMouseEntered(e -> setCursor(Pick.getMe().getCursor()));
        colorButton.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        return colorButton;
    }

    public Color getColor() {
        return ColorUtil.valueOf(getText());
    }

    public void setColor(Color color) {
        colorPane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void addRecentColor(Color color) {
        recentColors.remove(color);
        recentColors.add(color);
    }

    public String getText() {
        return hexField.getText();
    }

    public void setText(String text) {
        hexField.setText(text);
    }

    public StringProperty textProperty() {
        return hexField.textProperty();
    }
}
