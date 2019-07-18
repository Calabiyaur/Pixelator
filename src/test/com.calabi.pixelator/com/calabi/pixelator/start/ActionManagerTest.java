package com.calabi.pixelator.start;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import com.calabi.pixelator.res.Action;

import static org.junit.Assert.fail;

class ActionManagerTest {

    /**
     * Make sure that the key combination is unique for every registered action
     */
    @Test
    void testActions() {
        Set<Pair<Pair<Boolean, Boolean>, Pair<Boolean, KeyCode>>> combinations = new HashSet<>();

        for (Action action : Action.values()) {
            if (action.getKey() == null) {
                continue;
            }

            Pair<Pair<Boolean, Boolean>, Pair<Boolean, KeyCode>> c =
                    Pair.of(Pair.of(action.isCtrl(), action.isShift()), Pair.of(action.isAlt(), action.getKey()));

            if (!combinations.contains(c)) {
                combinations.add(c);
            } else {
                fail(action.name());
            }
        }
    }

}
