package com.calabi.pixelator.meta;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class HashMatrix<T> implements Matrix<T> {

    private Map<Integer, Map<Integer, T>> values;

    public HashMatrix() {
        values = new HashMap<>();
    }

    @Override
    public T get(int x, int y) {
        Map<Integer, T> row = values.get(y);
        return row == null ? null : row.get(x);
    }

    @Override
    public T put(int x, int y, T value) {
        return values.computeIfAbsent(y, i -> new HashMap<>()).put(x, value);
    }

    @Override
    public Map<Integer, T> getRow(int y) {
        cleanup();
        return values.get(y);
    }

    @Override
    public Map<Integer, T> getColumn(int x) {
        cleanup();
        return getColumnInternal(x);
    }

    private Map<Integer, T> getColumnInternal(int x) {
        Column<T> column = new Column<>(this, x);
        if (column.isEmpty()) {
            return null;
        }
        return column;
    }

    @Override
    public Set<Integer> rowIndices() {
        cleanup();
        return rowIndicesInternal();
    }

    private Set<Integer> rowIndicesInternal() {
        return values.keySet();
    }

    @Override
    public Set<Integer> columnIndices() {
        cleanup();
        return columnIndicesInternal();
    }

    private Set<Integer> columnIndicesInternal() {
        Set<Integer> columnIndices = new HashSet<>();
        for (Map.Entry<Integer, Map<Integer, T>> row : rowsInternal()) {
            columnIndices.addAll(row.getValue().keySet());
        }
        return columnIndices;
    }

    @Override
    public List<Map.Entry<Integer, Map<Integer, T>>> rows() {
        cleanup();
        return rowsInternal();
    }

    private List<Map.Entry<Integer, Map<Integer, T>>> rowsInternal() {
        return new EntryList<>(values.entrySet());
    }

    @Override
    public List<Map.Entry<Integer, Map<Integer, T>>> columns() {
        cleanup();
        return columnsInternal();
    }

    private List<Map.Entry<Integer, Map<Integer, T>>> columnsInternal() {
        Set<Map.Entry<Integer, Map<Integer, T>>> result = new HashSet<>();
        for (Integer x : columnIndicesInternal()) {
            result.add(new MatrixEntry<>(x, getColumnInternal(x)));
        }
        return new EntryList<>(result);
    }

    private void cleanup() {
        Iterator<Integer> iter = rowIndicesInternal().iterator();
        while (iter.hasNext()) {
            Integer i = iter.next();
            Map<Integer, T> row = values.get(i);
            if (row.isEmpty()) {
                iter.remove();
            }
        }
    }

    private class Column<U> extends AbstractMap<Integer, U> {

        private final HashMatrix<U> parent;
        private final int x;

        public Column(HashMatrix<U> parent, int x) {
            this.parent = parent;
            this.x = x;
        }

        @Override
        public Set<Entry<Integer, U>> entrySet() {
            Set<Entry<Integer, U>> result = new HashSet<>();
            for (Entry<Integer, Map<Integer, U>> row : parent.rows()) {
                U value = row.getValue().get(x);
                if (value != null) {
                    result.add(new MatrixEntry<>(row.getKey(), value));
                }
            }
            return result;
        }

        @Override
        public U put(Integer key, U value) {
            return parent.put(x, key, value);
        }

        @Override
        public void clear() {
            for (Entry<Integer, Map<Integer, U>> row : parent.rows()) {
                row.getValue().remove(x);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            Column<?> column = (Column<?>) o;
            return x == column.x && Objects.equals(parent, column.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), parent, x);
        }

        @Override
        public String toString() {
            return "x=" + x + ": " + super.toString();
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }
    }

    private class MatrixEntry<V> implements Map.Entry<Integer, V> {

        private Integer key;
        private V value;

        public MatrixEntry(Integer key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V before = this.value;
            this.value = value;
            return before;
        }
    }

    private class EntryList<W> extends ArrayList<Map.Entry<Integer, W>> {

        private final Set<Map.Entry<Integer, W>> set;

        public EntryList(Set<Map.Entry<Integer, W>> set) {
            this.set = set;
            super.addAll(set);
            super.sort(Comparator.comparingInt(e -> e.getKey()));
        }

        private void checkUnique(Map.Entry<Integer, W> entry) {
            if (contains(entry)) {
                throw new IllegalStateException();
            }
        }

        private void checkUnique(Collection<? extends Map.Entry<Integer, W>> entries) {
            if (!Collections.disjoint(set, entries)) {
                throw new IllegalStateException();
            }
        }

        @Override
        public Map.Entry<Integer, W> set(int index, Map.Entry<Integer, W> entry) {
            checkUnique(entry);
            set.add(entry);
            return super.set(index, entry);
        }

        @Override
        public boolean add(Map.Entry<Integer, W> entry) {
            checkUnique(entry);
            set.add(entry);
            return super.add(entry);
        }

        @Override
        public void add(int index, Map.Entry<Integer, W> entry) {
            checkUnique(entry);
            set.add(entry);
            super.add(index, entry);
        }

        @Override
        public Map.Entry<Integer, W> remove(int index) {
            Map.Entry<Integer, W> removedEntry = super.remove(index);
            set.remove(removedEntry);
            return removedEntry;
        }

        @Override
        public boolean remove(Object o) {
            set.remove(o);
            return super.remove(o);
        }

        @Override
        public void clear() {
            set.clear();
            super.clear();
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<Integer, W>> c) {
            checkUnique(c);
            set.addAll(c);
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Map.Entry<Integer, W>> c) {
            checkUnique(c);
            set.addAll(c);
            return super.addAll(index, c);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            set.removeAll(subList(fromIndex, toIndex));
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            set.removeAll(c);
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            set.retainAll(c);
            return super.retainAll(c);
        }

        @Override
        public boolean removeIf(Predicate<? super Map.Entry<Integer, W>> filter) {
            set.removeIf(filter);
            return super.removeIf(filter);
        }

        @Override
        public void replaceAll(UnaryOperator<Map.Entry<Integer, W>> operator) {
            throw new UnsupportedOperationException();
        }
    }
}
