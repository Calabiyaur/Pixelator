package com.calabi.pixelator.start;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.view.dialog.ErrorDialog;

public class ExceptionHandler {

    private static List<ErrorDialog> openDialogs = new ArrayList<>();

    private ExceptionHandler() {
    }

    public static void handle(Throwable e) {
        Logger.error(e);

        if (openDialogs.size() < 10) {
            ErrorDialog dialog = new ErrorDialog();
            dialog.setHeader(e.getClass().getSimpleName());
            dialog.setMessage(ExceptionUtils.getStackTrace(e));

            openDialogs.add(dialog);
            dialog.setOnHiding(event -> openDialogs.remove(dialog));
            dialog.show();
        }
    }

}
