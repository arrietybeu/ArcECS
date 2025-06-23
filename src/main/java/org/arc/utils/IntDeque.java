package org.arc.utils;

import java.util.Arrays;

/**
 * A resizable double-ended queue of integers optimized for ECS operations.
 * Provides efficient add/remove operations at both ends.
 * 
 * @author Arriety
 */
public class IntDeque {
    
    private int[] elements;
    private int head = 0;
    private int tail = 0;
    private int size = 0;
    
    /**
     * Creates a new deque with default initial capacity.
     */
    public IntDeque() {
        this(16);
    }
    
    /**
     * Creates a new deque with the specified initial capacity.
     * @param capacity the initial capacity
     */
    public IntDeque(int capacity) {
        elements = new int[capacity];
    }
    
    /**
     * Adds an element to the front of the deque.
     * @param element the element to add
     */
    public void addFirst(int element) {
        if (size == elements.length) {
            grow();
        }
        
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }
    
    /**
     * Adds an element to the back of the deque.
     * @param element the element to add
     */
    public void addLast(int element) {
        if (size == elements.length) {
            grow();
        }
        
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
        size++;
    }
    
    /**
     * Removes and returns the first element.
     * @return the first element
     * @throws IllegalStateException if the deque is empty
     */
    public int removeFirst() {
        if (size == 0) {
            throw new IllegalStateException("Deque is empty");
        }
        
        int element = elements[head];
        head = (head + 1) % elements.length;
        size--;
        return element;
    }
    
    /**
     * Removes and returns the last element.
     * @return the last element
     * @throws IllegalStateException if the deque is empty
     */
    public int removeLast() {
        if (size == 0) {
            throw new IllegalStateException("Deque is empty");
        }
        
        tail = (tail - 1 + elements.length) % elements.length;
        int element = elements[tail];
        size--;
        return element;
    }
    
    /**
     * Gets the first element without removing it.
     * @return the first element
     * @throws IllegalStateException if the deque is empty
     */
    public int peekFirst() {
        if (size == 0) {
            throw new IllegalStateException("Deque is empty");
        }
        return elements[head];
    }
    
    /**
     * Gets the last element without removing it.
     * @return the last element
     * @throws IllegalStateException if the deque is empty
     */
    public int peekLast() {
        if (size == 0) {
            throw new IllegalStateException("Deque is empty");
        }
        return elements[(tail - 1 + elements.length) % elements.length];
    }
    
    /**
     * Removes the first occurrence of the specified element.
     * @param element the element to remove
     * @return true if the element was removed, false otherwise
     */
    public boolean remove(int element) {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            if (elements[index] == element) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the deque contains the specified element.
     * @param element the element to check for
     * @return true if the element is found, false otherwise
     */
    public boolean contains(int element) {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            if (elements[index] == element) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the number of elements in the deque.
     * @return the size
     */
    public int size() {
        return size;
    }
    
    /**
     * Checks if the deque is empty.
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Clears all elements from the deque.
     */
    public void clear() {
        head = 0;
        tail = 0;
        size = 0;
    }
    
    /**
     * Converts the deque to an array.
     * @return an array containing all elements in order
     */
    public int[] toArray() {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = elements[(head + i) % elements.length];
        }
        return result;
    }
    
    /**
     * Removes the element at the specified position (relative to head).
     * @param position the position to remove from
     */
    private void removeAt(int position) {
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        if (position < size / 2) {
            // Shift elements from the front
            for (int i = position; i > 0; i--) {
                int fromIndex = (head + i - 1) % elements.length;
                int toIndex = (head + i) % elements.length;
                elements[toIndex] = elements[fromIndex];
            }
            head = (head + 1) % elements.length;
        } else {
            // Shift elements from the back
            for (int i = position; i < size - 1; i++) {
                int fromIndex = (head + i + 1) % elements.length;
                int toIndex = (head + i) % elements.length;
                elements[toIndex] = elements[fromIndex];
            }
            tail = (tail - 1 + elements.length) % elements.length;
        }
        size--;
    }
    
    /**
     * Grows the deque capacity.
     */
    private void grow() {
        int[] newElements = new int[elements.length * 2];
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[(head + i) % elements.length];
        }
        elements = newElements;
        head = 0;
        tail = size;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IntDeque{size=").append(size).append(", elements=[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[(head + i) % elements.length]);
        }
        sb.append("]}");
        return sb.toString();
    }
} 