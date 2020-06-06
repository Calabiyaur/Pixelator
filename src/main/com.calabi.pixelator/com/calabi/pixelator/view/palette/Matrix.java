package com.calabi.pixelator.view.palette;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Matrix<T> {

    T get(int x, int y);
    T put(int x, int y, T value);
    Map<Integer, T> getRow(int y);
    Map<Integer, T> getColumn(int x);
    Set<Integer> rowIndices();
    Set<Integer> columnIndices();
    List<Map.Entry<Integer, Map<Integer, T>>> rows();
    List<Map.Entry<Integer, Map<Integer, T>>> columns();

}
