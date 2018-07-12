package main.java.start;

import main.java.logging.Logger;
import main.java.view.dialog.ErrorDialog;

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
        dialog.setHeader(e.toString());
        dialog.setMessage(readStackTrace(e.getStackTrace()));
        dialog.show();
    }

    private static String readStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            builder.append(element.toString());
            builder.append('\n');
        }
        return builder.append('\n').toString();
    }

}
