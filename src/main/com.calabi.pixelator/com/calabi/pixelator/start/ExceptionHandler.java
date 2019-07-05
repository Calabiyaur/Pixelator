package com.calabi.pixelator.start;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.view.dialog.ErrorDialog;

public class ExceptionHandler {

    private static ExceptionHandler instance = new ExceptionHandler();

    private ExceptionHandler() {
    }

    public static void handle(Throwable e) {
        Logger.error(e);
        ErrorDialog dialog = new ErrorDialog();
        dialog.setHeader(e.getClass().getSimpleName());
        dialog.setMessage(ExceptionUtils.getStackTrace(e));
        dialog.show();
    }

}
