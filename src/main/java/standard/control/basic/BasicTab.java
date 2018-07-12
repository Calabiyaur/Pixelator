package main.java.standard.control.basic;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public abstract class BasicTab extends Pane {

    private TabToggle toggle;
    private Region content;

    public BasicTab(Region content) {
        super(content);
        this.content = content;
        content.prefWidthProperty().bind(widthProperty());
        content.prefHeightProperty().bind(heightProperty());
        toggle = createToggle();
        toggle.selectedProperty().addListener((ov, o, n) -> {
            if (n) {
                this.toFront();
            }
        });
    }

    protected abstract TabToggle createToggle();

    public void setText(String text) {
        toggle.setText(text);
    }

    public TabToggle getToggle() {
        return toggle;
    }

    protected Region getContent() {
        return content;
    }

}
