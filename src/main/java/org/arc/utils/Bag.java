package org.arc.utils;

import org.arc.utils.reflect.ArrayReflection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Arriety
 */
public class Bag<T> implements Iterable<T> {

    private T[] array;
    private int size = 0;

    public Bag() {
        this(64);
    }

    @SuppressWarnings("unchecked")
    public Bag(int capacity) {
        array = (T[])ArrayReflection.newInstance(Object.class, capacity);
    }

    public Bag(Class<T> type) {
        this(type, 64);
    }

    @SuppressWarnings("unchecked")
    public Bag(Class<T> type, int capacity) {
        array = (T[]) ArrayReflection.newInstance(type, capacity);
    }

    public void add(T item) {
        if (size == array.length) grow(array.length * 2);
        array[size++] = item;
    }

    public T get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException();
        return array[index];
    }

    public void set(int index, T item) {
        ensureCapacity(index + 1);
        array[index] = item;
        if (index >= size) size = index + 1;
    }

    public T remove(int index) {
        T removed = array[index];
        array[index] = array[--size];
        array[size] = null;
        return removed;
    }

    public boolean remove(T item) {
        for (int i = 0; i < size; i++) {
            if (array[i].equals(item)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void clear() {
        Arrays.fill(array, 0, size, null);
        size = 0;
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > array.length) grow(minCapacity);
    }

    private void grow(int newCapacity) {
        array = Arrays.copyOf(array, newCapacity);
    }

    public T[] getData() {
        return array;
    }

    public int getCapacity() {
        return array.length;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return array[cursor++];
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bag(");
        for (int i = 0; size > i; i++) {
            if (i > 0) sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bag bag = (Bag) o;
        if (size != bag.size())
            return false;

        for (int i = 0; size > i; i++) {
            if (array[i] != bag.array[i])
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0, s = size; s > i; i++) {
            hash = (127 * hash) + array[i].hashCode();
        }

        return hash;
    }

}
