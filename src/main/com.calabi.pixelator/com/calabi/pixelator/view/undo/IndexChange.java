package com.calabi.pixelator.view.undo;

import com.calabi.pixelator.ui.image.WritableImage;

/**
 * Undoable frame index change.
 * The following statements should always be true:
 * - An empty changelist cannot start with an IndexChange.
 * - A changelist cannot contain two subsequent IndexChanges.
 * - When an IndexChange gets added to a changelist whose last entry is already an IndexChange, then the two IndexChanges are
 *   merged into one instead of adding the second one. If the resulting IndexChange is empty, it is removed entirely from the
 *   changelist.
 */
public class IndexChange implements Undoable {

    private final WritableImage image;
    private int before;
    private int after;

    public IndexChange(WritableImage image, int before, int after) {
        this.image = image;
        this.before = before;
        this.after = after;
    }

    public void merge(IndexChange other) {
        after = other.after;
        other.before = before;
    }

    public void replace(IndexChange other) {
        before = other.before;
        after = other.after;
    }

    public IndexChange reverse() {
        return new IndexChange(image, after, before);
    }

    @Override
    public void undo() {
        image.setIndex(before);
    }

    @Override
    public void redo() {
        image.setIndex(after);
    }

    @Override
    public boolean isEmpty() {
        return before == after;
    }

    @Override
    public IndexChange copy() {
        return this;
    }

}
