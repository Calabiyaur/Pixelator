package main.java.util;

public class Do {

    public static void when(boolean n, Runnable r) {
        if (n) {
            r.run();
        }
    }

}
