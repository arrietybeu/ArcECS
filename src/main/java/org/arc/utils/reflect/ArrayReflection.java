package org.arc.utils.reflect;

import java.lang.reflect.Array;

/**
 * @author Arriety
 */
public class ArrayReflection {

    /**
     * Creates a new array with the specified component type and length.
     */
    static public Object newInstance(Class c, int size) {
        return Array.newInstance(c, size);
    }

    /**
     * Returns the length of the supplied array.
     */
    static public int getLength(Object array) {
        return Array.getLength(array);
    }

    /**
     * Returns the value of the indexed component in the supplied array.
     */
    static public Object get(Object array, int index) {
        return Array.get(array, index);
    }

    /**
     * Sets the value of the indexed component in the supplied array to the supplied value.
     */
    static public void set(Object array, int index, Object value) {
        Array.set(array, index, value);
    }

}
