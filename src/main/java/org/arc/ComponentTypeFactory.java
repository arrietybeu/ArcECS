package org.arc;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating and managing component types.
 * Ensures that each component class gets exactly one ComponentType instance.
 * 
 * @author Arriety
 */
public final class ComponentTypeFactory {
    
    private static final ConcurrentHashMap<Class<? extends Component>, ComponentType> componentTypes = 
            new ConcurrentHashMap<>();
    
    private ComponentTypeFactory() {
        // Utility class
    }
    
    /**
     * Gets or creates a component type for the given component class.
     * @param componentClass the component class
     * @return the component type
     */
    public static ComponentType getTypeFor(Class<? extends Component> componentClass) {
        return componentTypes.computeIfAbsent(componentClass, ComponentType::new);
    }
    
    /**
     * Gets the total number of registered component types.
     * @return the number of component types
     */
    public static int getTypeCount() {
        return componentTypes.size();
    }
    
    /**
     * Clears all registered component types. Use with caution.
     */
    public static void clear() {
        componentTypes.clear();
    }
} 