package main.java.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import main.java.res.Config;
import main.java.res.Images;
import main.java.control.basic.ToggleImageButton;
import main.java.control.basic.BasicText;
import main.java.control.parent.BasicWindow;
import main.java.control.image.PixelatedImageView;
import main.java.view.tool.Tools;

public class ToolView extends GridPane {

    private static ToolView instance;
    private static ObjectProperty<Tools> currentTool = new SimpleObjectProperty<>();
    private static CheckBox replace;
    private static CheckBox fill;
    private static BooleanProperty replaceColor = new SimpleBooleanProperty();
    private static BooleanProperty fillShape = new SimpleBooleanProperty();
    private static Label preview = new Label();
    private static Label previewTool = new Label();
    private static Label previewSelection = new Label();
    private static DoubleProperty previewSize = new SimpleDoubleProperty(160);
    private static BasicText widthText = new BasicText("Width", "");
    private static BasicText heightText = new BasicText("Height", "");
    private static BasicText zoomText = new BasicText("Zoom", "100 %");

    public static ToolView getInstance() {
        if (instance == null) {
            instance = new ToolView();
            instance.setStyle("-fx-background-color: #f4f4f4");
            instance.setVgap(6);
            instance.setPrefWidth(210);
            instance.setPadding(new Insets(BasicWindow.RESIZE_MARGIN));
            Platform.runLater(() -> instance.setPrefWidth(instance.getWidth()));
            previewSize.bind(instance.prefWidthProperty().subtract(2 * BasicWindow.RESIZE_MARGIN));

            instance.addRow(0, new Label("TOOLS"));
            ToggleGroup tg = new ToggleGroup();
            tg.selectedToggleProperty().addListener((ov, o, n) -> {
                if (n == null && o != null) {
                    o.setSelected(true);
                }
            });
            ToggleButton pen = new ToggleImageButton(tg, Images.PEN);
            pen.setOnAction(e -> currentTool.set(Tools.PEN));
            ToggleButton line = new ToggleImageButton(tg, Images.LINE);
            line.setOnAction(e -> currentTool.set(Tools.LINE));
            ToggleButton fill = new ToggleImageButton(tg, Images.FILL);
            fill.setOnAction(e -> currentTool.set(Tools.FILL));
            ToggleButton pick = new ToggleImageButton(tg, Images.PICK);
            pick.setOnAction(e -> currentTool.set(Tools.PICK));
            ToggleButton select = new ToggleImageButton(tg, Images.SELECT);
            select.setOnAction(e -> currentTool.set(Tools.SELECT));
            ToggleButton wand = new ToggleImageButton(tg, Images.WAND);
            wand.setOnAction(e -> currentTool.set(Tools.WAND));
            ToggleButton rectangle = new ToggleImageButton(tg, Images.RECTANGLE);
            rectangle.setOnAction(e -> currentTool.set(Tools.RECTANGLE));
            ToggleButton ellipse = new ToggleImageButton(tg, Images.ELLIPSE);
            ellipse.setOnAction(e -> currentTool.set(Tools.ELLIPSE));
            ToggleButton fillColor = new ToggleImageButton(tg, Images.FILL_COLOR);
            fillColor.setOnAction(e -> currentTool.set(Tools.FILL_COLOR));

            FlowPane tools1 = new FlowPane(pen, line, pick, fill, fillColor, rectangle, ellipse);
            tools1.setVgap(6);
            tools1.setHgap(6);
            instance.addRow(1, tools1);
            pen.fire();

            instance.addRow(2, new Separator());

            FlowPane tools2 = new FlowPane(select, wand);
            tools2.setVgap(6);
            tools2.setHgap(6);
            instance.addRow(3, tools2);

            instance.addRow(4, new Separator());

            replace = new CheckBox("Replace");
            replace.setOnAction(e -> replaceColor.set(replace.isSelected()));
            ToolView.fill = new CheckBox("Fill shape");
            ToolView.fill.setOnAction(e -> fillShape.set(ToolView.fill.isSelected()));
            VBox prefBox = new VBox(replace, ToolView.fill);
            prefBox.setSpacing(3);
            instance.addRow(5, prefBox);

            instance.addRow(6, new Separator());

            GridPane previewGrid = new GridPane();
            previewGrid.addRow(0, new Label("PREVIEW"));

            StackPane previewStack = new StackPane(preview, previewTool, previewSelection);
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
            clip.widthProperty().bind(previewSize);
            clip.heightProperty().bind(previewSize);
            previewStack.setClip(clip);
            previewStack.setAlignment(Pos.TOP_LEFT);
            previewStack.minWidthProperty().bind(previewSize);

            ObservableValue<? extends Number> hProp = getPreviewBinding();
            previewStack.minHeightProperty().bind(hProp);
            previewStack.prefWidthProperty().bind(previewSize);
            previewStack.prefHeightProperty().bind(hProp);

            previewGrid.addRow(1, previewStack);
            previewGrid.addRow(2, widthText);
            previewGrid.addRow(3, heightText);
            previewGrid.addRow(4, zoomText);
            GridPane.setHalignment(previewStack, HPos.CENTER);
            previewGrid.visibleProperty().bind(preview.graphicProperty().isNotNull());
            GridPane.setMargin(previewStack, new Insets(6, 0, 6, 0));
            previewGrid.setPadding(new Insets(4, 0, 10, 0));
            instance.addRow(7, previewGrid);

            initConfig();
        }
        return instance;
    }

    private static void initConfig() {
        setReplace(Config.getBoolean(Config.REPLACE, false));
        setFillShape(Config.getBoolean(Config.FILL_SHAPE, false));
        replaceColor.addListener((ov, o, n) -> Config.putBoolean(Config.REPLACE, n));
        fillShape.addListener((ov, o, n) -> Config.putBoolean(Config.FILL_SHAPE, n));
    }

    public static void setPreview(Image image, Image toolImage, Image selectionImage) {
        if (image == null) {
            preview.setGraphic(null);
            previewTool.setGraphic(null);
            previewSelection.setGraphic(null);
        } else {
            preview.setGraphic(new PixelatedImageView(image));
            preview.setTranslateX(0);
            preview.setTranslateY(0);
            previewTool.setGraphic(new PixelatedImageView(toolImage));
            previewTool.setTranslateX(0);
            previewTool.setTranslateY(0);
            previewSelection.setGraphic(new PixelatedImageView(selectionImage));
            previewSelection.setTranslateX(0);
            previewSelection.setTranslateY(0);
            //TODO: Re-format the preview stack so it can fit itself to the new image height
        }
    }

    public static void setPreviewPosition(double x, double y) {
        if (preview.getWidth() > previewSize.get()) {
            double xTranslate =
                    -Math.min(Math.max(0, x - previewSize.get() / 2), preview.getWidth() - previewSize.get());
            preview.setTranslateX(Math.round(xTranslate));
            previewTool.setTranslateX(Math.round(xTranslate));
            previewSelection.setTranslateX(Math.round(xTranslate));
        } else {
            preview.setTranslateX(0);
            previewTool.setTranslateX(0);
            previewSelection.setTranslateX(0);
        }

        if (preview.getHeight() > previewSize.get()) {
            double yTranslate =
                    -Math.min(Math.max(0, y - previewSize.get() / 2), preview.getHeight() - previewSize.get());
            preview.setTranslateY(Math.round(yTranslate));
            previewTool.setTranslateY(Math.round(yTranslate));
            previewSelection.setTranslateY(Math.round(yTranslate));
        } else {
            preview.setTranslateY(0);
            previewTool.setTranslateY(0);
            previewSelection.setTranslateY(0);
        }
    }

    private static ObservableValue<? extends Number> getPreviewBinding() {
        return Bindings.createDoubleBinding(() -> {
            PixelatedImageView graphic = (PixelatedImageView) preview.getGraphic();
            if (graphic == null) {
                return previewSize.get();
            } else {
                return Math.min(graphic.getImage().getHeight(), previewSize.get());
            }
        }, previewSize, preview.graphicProperty());
    }

    public static void setSize(int width, int height) {
        widthText.setValue(Integer.toString(width));
        heightText.setValue(Integer.toString(height));
    }

    public static void setZoom(double zoom) {
        zoomText.setValue(Long.toString(Math.round(zoom * 100)) + " %");
    }

    public static ObjectProperty<Tools> currentToolProperty() {
        return currentTool;
    }

    public static Tools getCurrentTool() {
        return currentTool.get();
    }

    public static boolean isReplaceColor() {
        return replaceColor.get();
    }

    public static BooleanProperty replaceColorProperty() {
        return replaceColor;
    }

    public static boolean isFillShape() {
        return fillShape.get();
    }

    public static void setReplace(boolean replaceColor) {
        if (replaceColor) {
            ToolView.replace.fire();
        }
    }

    public static void setFillShape(boolean fillShape) {
        if (fillShape) {
            ToolView.fill.fire();
        }
    }
}
