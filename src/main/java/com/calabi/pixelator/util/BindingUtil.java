package com.calabi.pixelator.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javafx.beans.property.Property;

public final class BindingUtil {

    private static final Set<Set<Property<?>>> LOCKS = new HashSet<>();

    public static <T, U> void bindBidirectional(Property<T> prop1, Property<U> prop2, Function<T, U> conv1, Function<U, T> conv2) {
        bind(prop1, prop2, conv1);
        bind(prop2, prop1, conv2);
    }

    private static <T, U> void bind(Property<T> prop1, Property<U> prop2, Function<T, U> conv) {
        prop1.addListener((ov, o, n) -> {
            Set<Property<?>> lock = Set.of(prop1, prop2);
            if (!LOCKS.contains(lock)) {
                LOCKS.add(lock);
                prop2.setValue(conv.apply(prop1.getValue()));
                LOCKS.remove(lock);
            }
        });
    }

}
