package main.java.util;

public class Do {

    public static void when(boolean when, Runnable then) {
        if (when) {
            then.run();
        }
    }

    public static void when(boolean when, Runnable then, Runnable otherwise) {
        if (when) {
            then.run();
        } else {
            otherwise.run();
        }
    }

}
