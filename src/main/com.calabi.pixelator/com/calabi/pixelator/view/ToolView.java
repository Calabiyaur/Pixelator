package com.calabi.pixelator.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import com.calabi.pixelator.control.basic.BasicCheckBox;
import com.calabi.pixelator.control.basic.BasicIntegerField;
import com.calabi.pixelator.control.basic.ToggleImageButton;
import com.calabi.pixelator.control.image.PixelatedImageView;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.view.tool.Tools;

public class ToolView extends VBox {

    private static ToolView instance;
    private ObjectProperty<Tools> currentTool = new SimpleObjectProperty<>();
    private BasicCheckBox replaceColorField;
    private BasicCheckBox fillShapeField;
    private BasicIntegerField shapeWidthField;
    private BooleanProperty replaceColor = new SimpleBooleanProperty();
    private BooleanProperty fillShape = new SimpleBooleanProperty();
    private IntegerProperty shapeWidth = new SimpleIntegerProperty();
    private Label preview = new Label();
    private Label previewTool = new Label();
    private Label previewSelection = new Label();
    private Pane clipWrapper;
    private Text sizeText = new Text();
    private Text zoomText = new Text();

    private ToolView() {
        setStyle("-fx-background-color: #f4f4f4");
        setSpacing(6);
        setPrefWidth(210);
        setPadding(new Insets(6, 0, 6, 6));

        getChildren().add(new Label("TOOLS"));
        ToggleGroup tg = new ToggleGroup();
        tg.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null && o != null) {
                o.setSelected(true);
            }
        });

        FlowPane tools1 = createFirstToolLayer(tg);
        tools1.setVgap(1);
        tools1.setHgap(1);
        getChildren().add(1, tools1);

        getChildren().add(2, new Separator());

        FlowPane tools2 = createSecondToolLayer(tg);
        tools2.setVgap(1);
        tools2.setHgap(1);
        getChildren().add(3, tools2);

        getChildren().add(4, new Separator());

        VBox prefBox = createPrefLayer();
        prefBox.setFillWidth(false);
        prefBox.setSpacing(3);
        getChildren().add(5, prefBox);

        getChildren().add(6, new Separator());

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
        getChildren().add(7, previewGrid);

        initConfig();
    }

    public static ToolView getInstance() {
        if (instance == null) {
            instance = new ToolView();
        }
        return instance;
    }

    private FlowPane createFirstToolLayer(ToggleGroup tg) {
        ToggleButton pen = new ToggleImageButton(tg, Images.PEN);
        pen.setOnAction(e -> currentTool.set(Tools.PEN));
        ToggleButton line = new ToggleImageButton(tg, Images.LINE);
        line.setOnAction(e -> currentTool.set(Tools.LINE));
        ToggleButton fill = new ToggleImageButton(tg, Images.FILL);
        fill.setOnAction(e -> currentTool.set(Tools.FILL));
        ToggleButton pick = new ToggleImageButton(tg, Images.PICK);
        pick.setOnAction(e -> currentTool.set(Tools.PICK));
        ToggleButton rectangle = new ToggleImageButton(tg, Images.RECTANGLE);
        rectangle.setOnAction(e -> currentTool.set(Tools.RECTANGLE));
        ToggleButton ellipse = new ToggleImageButton(tg, Images.ELLIPSE);
        ellipse.setOnAction(e -> currentTool.set(Tools.ELLIPSE));
        ToggleButton fillColor = new ToggleImageButton(tg, Images.FILL_COLOR);
        fillColor.setOnAction(e -> currentTool.set(Tools.FILL_COLOR));

        FlowPane tools1 = new FlowPane(pen, line, pick, fill, fillColor, rectangle, ellipse);
        pen.fire();
        return tools1;
    }

    private FlowPane createSecondToolLayer(ToggleGroup tg) {
        ToggleButton select = new ToggleImageButton(tg, Images.SELECT);
        select.setOnAction(e -> currentTool.set(Tools.SELECT));
        ToggleButton wand = new ToggleImageButton(tg, Images.WAND);
        wand.setOnAction(e -> currentTool.set(Tools.WAND));
        ToggleButton fillSelect = new ToggleImageButton(tg, Images.FILL_SELECT);
        fillSelect.setOnAction(e -> currentTool.set(Tools.FILL_SELECT));

        return new FlowPane(select, wand, fillSelect);
    }

    private VBox createPrefLayer() {
        replaceColorField = new BasicCheckBox("Replace");
        fillShapeField = new BasicCheckBox("Fill shape");
        shapeWidthField = new BasicIntegerField("Width");
        shapeWidthField.setMinValue(1);
        shapeWidthField.setMaxValue(10);
        replaceColorField.valueProperty().addListener((ov, o, n) -> replaceColor.set(n));
        fillShapeField.valueProperty().addListener((ov, o, n) -> fillShape.set(n));
        shapeWidthField.valueProperty().addListener((ov, o, n) -> shapeWidth.set(n));
        return new VBox(replaceColorField, fillShapeField, shapeWidthField);
    }

    private void initConfig() {
        setReplace(Config.REPLACE.getBoolean());
        setFillShape(Config.FILL_SHAPE.getBoolean());
        setShapeWidth(Config.SHAPE_WIDTH.getInt());
        replaceColor.addListener((ov, o, n) -> Config.REPLACE.putBoolean(n));
        fillShape.addListener((ov, o, n) -> Config.FILL_SHAPE.putBoolean(n));
        shapeWidth.addListener((ov, o, n) -> Config.SHAPE_WIDTH.putInt(n.intValue()));
    }

    public void setPreview(Image image, Image toolImage, Image selectionImage) {
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

    public void setPreviewPosition(double x, double y) {
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

    public void setSize(int width, int height) {
        sizeText.setText(Integer.toString(width) + "×" + Integer.toString(height));
    }

    public void setZoom(double zoom) {
        zoomText.setText(Long.toString(Math.round(zoom * 100)) + " %");
    }

    public ObjectProperty<Tools> currentToolProperty() {
        return currentTool;
    }

    public Tools getCurrentTool() {
        return currentTool.get();
    }

    public boolean isReplaceColor() {
        return replaceColor.get();
    }

    public BooleanProperty replaceColorProperty() {
        return replaceColor;
    }

    public boolean isFillShape() {
        return fillShape.get();
    }

    public void setReplace(boolean value) {
        replaceColorField.setValue(value);
    }

    public void setFillShape(boolean value) {
        fillShapeField.setValue(value);
    }

    public void setShapeWidth(int value) {
        shapeWidthField.setValue(value);
    }

}