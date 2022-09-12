package com.calabi.pixelator.meta;

public interface QuadConsumer<K, V, S, T> {

    void accept(K k, V v, S s, T t);
}
