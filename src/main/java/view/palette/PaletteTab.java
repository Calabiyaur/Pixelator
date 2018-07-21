package main.java.view.palette;

import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;

import main.java.control.image.PixelatedImageView;
import main.java.control.parent.BasicScrollPane;
import main.java.control.parent.BasicTab;
import main.java.control.parent.TabToggle;
import main.java.meta.Direction;

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

    public PaletteEditor getEditor() {
        return editor;
    }

    public void setEditor(PaletteEditor editor) {
        this.editor = editor;
    }

    private class PaletteToggle extends TabToggle {

        public PaletteToggle() {
            super(Direction.WEST);
            setClosable(true);
        }

    }
}
