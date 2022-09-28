package com.calabi.pixelator.util.meta;

public interface QuadConsumer<K, V, S, T> {

    void accept(K k, V v, S s, T t);
}
