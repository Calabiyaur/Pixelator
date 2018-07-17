package main.java.start;

import main.java.logging.Logger;
import main.java.view.dialog.ErrorDialog;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionHandler {

    private static ExceptionHandler instance = new ExceptionHandler();

    private ExceptionHandler() {
    }

    public static ExceptionHandler get() {
        return instance;
    }

    public static void handle(Throwable e) {
        Logger.error(e);
        ErrorDialog dialog = new ErrorDialog();
        dialog.setHeader(e.getClass().getSimpleName());
        dialog.setMessage(ExceptionUtils.getStackTrace(e));
        dialog.show();
    }

}
