package main.java.view.tool;

public class PickSelect extends Select {

    private static PickSelect me = new PickSelect();

    public PickSelect() {
        draggableAfterClick = false;
        selectionTool = false;
    }

    public static PickSelect getMe() {
        return me;
    }

    @Override public void releasePrimary() {
        if (isStillSincePress() && getSelectionLayer().isEmpty()) {
            Pick.getMe().releasePrimary();
        } else {
            Select.getMe().releasePrimary();
        }
    }
}
