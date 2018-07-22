package main.java.view.palette;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.image.Image;

import main.java.control.image.PixelatedImageView;
import main.java.control.parent.BasicScrollPane;
import main.java.control.parent.BasicTab;
import main.java.control.parent.TabToggle;
import main.java.files.Files;
import main.java.meta.Direction;
import main.java.meta.Point;
import main.java.res.PaletteConfig;
import main.java.view.dialog.SaveRequestDialog;

public class PaletteTab extends BasicTab {

    private PaletteEditor editor;

    public PaletteTab(PaletteEditor editor) {
        super(new BasicScrollPane(editor));
        this.editor = editor;

        PixelatedImageView imageView = editor.getImageView();
        Image image = editor.getImage();
        imageView.scaleXProperty().bind(Bindings.min(getContent().widthProperty().divide(image.widthProperty()), 24));
        imageView.scaleYProperty().bind(imageView.scaleXProperty());
        imageView.translateXProperty().bind(image.widthProperty()
                .multiply(imageView.scaleXProperty()).subtract(image.widthProperty()).divide(2));
        imageView.translateYProperty().bind(image.heightProperty()
                .multiply(imageView.scaleYProperty()).subtract(image.heightProperty()).divide(2));

        BasicScrollPane content = (BasicScrollPane) getContent();
        content.setStyle("-fx-background-color: #f4f4f4");
        content.setScrollByMouse(true);
    }

    @Override protected TabToggle createToggle() {
        return new PaletteToggle();
    }

    public void initConfig() {
        String x = getEditor().getFile().getProperties().getProperty(PaletteConfig.SELECTED_X.name());
        String y = getEditor().getFile().getProperties().getProperty(PaletteConfig.SELECTED_Y.name());
        if (x != null && y != null) {
            getEditor().select(new Point(Integer.parseInt(x), Integer.parseInt(y)));
        }
    }

    private void updateConfig() {
        Point point = getEditor().getSelected();
        getEditor().getFile().getProperties().put(PaletteConfig.SELECTED_X.name(), Integer.toString(point.getX()));
        getEditor().getFile().getProperties().put(PaletteConfig.SELECTED_Y.name(), Integer.toString(point.getY()));
    }

    public PaletteEditor getEditor() {
        return editor;
    }

    public void setEditor(PaletteEditor editor) {
        this.editor = editor;
    }

    public boolean closeIfClean() {
        if (isDirty()) {
            switch(SaveRequestDialog.display()) {
                case OK:
                    saveAndUndirty();
                    break;
                case NO:
                    Files.get().saveConfig(getEditor().getFile());
                    break;
                case CANCEL:
                    return false;
            }
        }
        return true;
    }

    public void saveAndUndirty() {
        updateConfig();
        Files.get().save(getEditor().getFile());
        undirty();
    }

    public void undirty() {
        getEditor().undirty();
    }

    public boolean isDirty() {
        return dirtyProperty().get();
    }

    public BooleanProperty dirtyProperty() {
        return getEditor().dirtyProperty();
    }

    private class PaletteToggle extends TabToggle {

        public PaletteToggle() {
            super(Direction.WEST);
            setClosable(true);
        }

    }
}
