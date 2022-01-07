package com.calabi.pixelator.view.dialog;

import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.calabi.pixelator.start.Pixelator;

public class SaveRequestDialog extends BasicDialog {

    private static Result answer;

    private SaveRequestDialog(int minWidth, int minHeight) {
        super(minWidth, minHeight);
        addContent(new Text("There are unsaved changes. \nDo you wish to save before closing?"), 0, 0);
        setOkText("Yes");
    }

    @Override
    public void focus() {

    }

    public static Result display() {
        answer = Result.CANCEL;

        Stage exit = new Stage();
        exit.setTitle("Unsaved changes");
        exit.initModality(Modality.APPLICATION_MODAL);
        exit.initOwner(Pixelator.getPrimaryStage());
        exit.setResizable(false);

        SaveRequestDialog dialog = new SaveRequestDialog(200, 140);
        dialog.setMinSize(200, 140);

        dialog.setOnOk(e -> {
            answer = Result.OK;
            dialog.close();
            exit.close();
        });

        dialog.addNoButton("No", e -> {
            answer = Result.NO;
            dialog.close();
            exit.close();
        });

        dialog.setOnCancel(e -> {
            answer = Result.CANCEL;
            dialog.close();
            exit.close();
        });

        exit.setScene(new Scene(dialog.getDialogPane()));
        exit.showAndWait();

        return answer;
    }
}
