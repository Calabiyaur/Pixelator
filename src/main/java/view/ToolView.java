package main.java.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import main.java.control.basic.ToggleImageButton;
import main.java.control.image.PixelatedImageView;
import main.java.res.Config;
import main.java.res.Images;
import main.java.view.tool.Tools;

public class ToolView extends VBox {

    private static ToolView instance;
    private static ObjectProperty<Tools> currentTool = new SimpleObjectProperty<>();
    private static CheckBox replace;
    private static CheckBox fill;
    private static BooleanProperty replaceColor = new SimpleBooleanProperty();
    private static BooleanProperty fillShape = new SimpleBooleanProperty();
    private static Label preview = new Label();
    private static Label previewTool = new Label();
    private static Label previewSelection = new Label();
    private static Pane clipWrapper;
    private static Text sizeText = new Text();
    private static Text zoomText = new Text();

    public static ToolView getInstance() {
        if (instance == null) {
            instance = new ToolView();
            instance.setStyle("-fx-background-color: #f4f4f4");
            instance.setSpacing(6);
            instance.setPrefWidth(210);
            instance.setPadding(new Insets(6, 0, 6, 6));

            instance.getChildren().add(new Label("TOOLS"));
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
            ToggleButton fillSelect = new ToggleImageButton(tg, Images.FILL_SELECT);
            fillSelect.setOnAction(e -> currentTool.set(Tools.FILL_SELECT));

            FlowPane tools1 = new FlowPane(pen, line, pick, fill, fillColor, rectangle, ellipse);
            tools1.setVgap(1);
            tools1.setHgap(1);
            instance.getChildren().add(1, tools1);
            pen.fire();

            instance.getChildren().add(2, new Separator());

            FlowPane tools2 = new FlowPane(select, wand, fillSelect);
            tools2.setVgap(1);
            tools2.setHgap(1);
            instance.getChildren().add(3, tools2);

            instance.getChildren().add(4, new Separator());

            replace = new CheckBox("Replace");
            replace.setOnAction(e -> replaceColor.set(replace.isSelected()));
            ToolView.fill = new CheckBox("Fill shape");
            ToolView.fill.setOnAction(e -> fillShape.set(ToolView.fill.isSelected()));
            VBox prefBox = new VBox(replace, ToolView.fill);
            prefBox.setSpacing(3);
            instance.getChildren().add(5, prefBox);

            instance.getChildren().add(6, new Separator());

            clipWrapper = new Pane();
            VBox.setVgrow(clipWrapper, Priority.ALWAYS);

            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(clipWrapper.widthProperty());
            clip.heightProperty().bind(clipWrapper.heightProperty());

            StackPane previewStack = new StackPane(preview, previewTool, previewSelection);
            clipWrapper.getChildren().add(previewStack);
            previewStack.setClip(clip);
            previewStack.setAlignment(Pos.TOP_LEFT);

            clipWrapper.maxWidthProperty().bind(previewStack.widthProperty());
            clipWrapper.maxHeightProperty().bind(previewStack.heightProperty());

            Region space = new Region();
            HBox detailBox = new HBox(sizeText, space, zoomText);
            HBox.setHgrow(space, Priority.ALWAYS);
            VBox previewGrid = new VBox(new Label("PREVIEW"), clipWrapper, detailBox);
            VBox.setVgrow(previewGrid, Priority.ALWAYS);

            previewGrid.visibleProperty().bind(preview.graphicProperty().isNotNull());
            instance.getChildren().add(7, previewGrid);

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
            //TODO: Re-format the preview stack so it can fit itself to the new image size
        }
    }

    public static void setPreviewPosition(double x, double y) {
        if (preview.getWidth() > clipWrapper.getWidth()) {
            double xTranslate =
                    -Math.min(Math.max(0, x - clipWrapper.getWidth() / 2), preview.getWidth() - clipWrapper.getWidth());
            preview.setTranslateX(Math.round(xTranslate));
            previewTool.setTranslateX(Math.round(xTranslate));
            previewSelection.setTranslateX(Math.round(xTranslate));
        } else {
            preview.setTranslateX(0);
            previewTool.setTranslateX(0);
            previewSelection.setTranslateX(0);
        }

        if (preview.getHeight() > clipWrapper.getHeight()) {
            double yTranslate =
                    -Math.min(Math.max(0, y - clipWrapper.getHeight() / 2), preview.getHeight() - clipWrapper.getHeight());
            preview.setTranslateY(Math.round(yTranslate));
            previewTool.setTranslateY(Math.round(yTranslate));
            previewSelection.setTranslateY(Math.round(yTranslate));
        } else {
            preview.setTranslateY(0);
            previewTool.setTranslateY(0);
            previewSelection.setTranslateY(0);
        }
    }

    public static void setSize(int width, int height) {
        sizeText.setText(Integer.toString(width) + "×" + Integer.toString(height));
    }

    public static void setZoom(double zoom) {
        zoomText.setText(Long.toString(Math.round(zoom * 100)) + " %");
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
