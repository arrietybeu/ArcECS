package org.arc;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a component type in the ECS framework.
 * Each component class gets a unique type with an index for efficient operations.
 * 
 * @author Arriety
 */
@Getter
public final class ComponentType {
    
    private static final AtomicInteger typeIndex = new AtomicInteger(0);

    /**
     * -- GETTER --
     *  Gets the component class this type represents.
     *
     * @return the component class
     */
    private final Class<? extends Component> componentClass;
    /**
     * -- GETTER --
     *  Gets the unique index of this component type.
     *
     * @return the type index
     */
    private final int index;
    /**
     * -- GETTER --
     *  Gets the name of this component type.
     *
     * @return the type name
     */
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