package com.calabi.pixelator.util;

public final class Param<T> {

    private final Class<T> type;
    private final T object;

    public Param(Class<T> type, T object) {
        Check.notNull(type);
        if (object != null) {
            Check.ensure(type.isAssignableFrom(object.getClass()));
        }
        this.type = type;
        this.object = object;
    }

    public Class<T> getType() {
        return type;
    }

    public T getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "(" + type + ") " + object;
    }

}
