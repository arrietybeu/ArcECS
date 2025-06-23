package org.arc.utils.reflect;

/**
 * @author Arriety
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ClassReflection {

    public static Class forName(String name) throws ReflectionException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException("Class not found: " + name, e);
        }
    }

    public static <T> T newInstance(Class<T> c) throws ReflectionException {
        try {
            return c.newInstance();
        } catch (InstantiationException e) {
            String help = ". Make sure class has a public no-arg constructor.";
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName() + help, e);
        } catch (IllegalAccessException e) {
            String help = ". Make sure class has a public no-arg constructor.";
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName() + help, e);
        }
    }

}
