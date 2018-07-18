package main.java.view.tool;

public class None extends Tool {

    private static None me = new None();

    private None() {
        super(
                null,
                null,
                0,
                0,
                false,
                false
        );
    }

    public static None getMe() {
        return me;
    }

    @Override public void pressPrimary() {
        // Do nothing.
    }

    @Override public void dragPrimary() {
        // Do nothing.
    }

    @Override public void releasePrimary() {
        // Do nothing.
    }

}
