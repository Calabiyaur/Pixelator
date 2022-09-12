package com.calabi.pixelator.start;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.calabi.pixelator.files.FileException;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.view.dialog.BasicDialog;
import com.calabi.pixelator.view.dialog.ErrorDialog;
import com.calabi.pixelator.view.dialog.MessageDialog;

public class ExceptionHandler {

    private static int openDialogs;

    private ExceptionHandler() {
    }

    public static void handle(Throwable e) {
        Logger.error(e);

        if (openDialogs < 10) {

            openDialogs++;
            BasicDialog dialog;

            if (e instanceof FileException) {
                dialog = new MessageDialog(e.getMessage(),
                        "There was a problem when trying to save this file.\n\n"
                                + "Make sure you have the permissions needed to write into the selected directory.");
            } else {
                dialog = new ErrorDialog();
                ((ErrorDialog) dialog).setHeader(e.getClass().getSimpleName());
                ((ErrorDialog) dialog).setMessage(ExceptionUtils.getStackTrace(e));
            }

            dialog.setOnHiding(event -> openDialogs--);
            dialog.show();
        }
    }

}
