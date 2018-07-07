package main.java.view.dialog;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;

import main.java.standard.control.basic.BasicCheckBox;
import main.java.standard.control.basic.BasicDialog;
import main.java.standard.control.basic.BasicTextField;
import main.java.standard.control.BiasButton;
import main.java.standard.Direction;

public class ResizeDialog extends BasicDialog {

    private BasicTextField wPercentField;
    private BasicTextField widthField;
    private BasicTextField hPercentField;
    private BasicTextField heightField;
    private BasicCheckBox keepRatio;
    private BiasButton biasButton;

    public ResizeDialog(int width, int height) {
        setTitle("Resize");
        setOkText("Resize");

        wPercentField = new BasicTextField("Width", "%", 100);
        widthField = new BasicTextField(null, "pixels", width);
        hPercentField = new BasicTextField("Height", "%", 100);
        heightField = new BasicTextField(null, "pixels", height);
        keepRatio = new BasicCheckBox("Keep ratio", false);
        biasButton = new BiasButton();

        addContent(wPercentField, 0, 0);
        addContent(hPercentField, 0, 1);
        addContent(widthField, 1, 0);
        addContent(heightField, 1, 1);
        addContent(keepRatio, 0, 2);
        addContent(biasButton, 1, 2);
        GridPane.setValignment(keepRatio, VPos.TOP);
        GridPane.setHalignment(biasButton, HPos.CENTER);
    }

    @Override public void focus() {
        widthField.focus();
    }

    public Integer getNewWidth() {
        return widthField.getIntValue();
    }

    public Integer getNewHeight() {
        return heightField.getIntValue();
    }

    public Direction getBias() {
        return biasButton.getBias();
    }

}
