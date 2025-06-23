package org.arc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a component type in the ECS framework.
 * Each component class gets a unique type with an index for efficient operations.
 * 
 * @author Arriety
 */
public final class ComponentType {
    
    private static final AtomicInteger typeIndex = new AtomicInteger(0);
    
    private final Class<? extends Component> componentClass;
    private final int index;
    private final String name;
    
    /**
     * Creates a new component type.
     * @param componentClass the component class this type represents
     */
    public ComponentType(Class<? extends Component> componentClass) {
        this.componentClass = componentClass;
        this.index = typeIndex.getAndIncrement();
        this.name = componentClass.getSimpleName();
    }
    
    /**
     * Gets the component class this type represents.
     * @return the component class
     */
    public Class<? extends Component> getComponentClass() {
        return componentClass;
    }
    
    /**
     * Gets the unique index of this component type.
     * @return the type index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Gets the name of this component type.
     * @return the type name
     */
    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ComponentType that = (ComponentType) obj;
        return index == that.index;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }
    
    @Override
    public String toString() {
        return "ComponentType{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }
} 