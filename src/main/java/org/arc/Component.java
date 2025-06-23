package org.arc;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all components in the ECS framework.
 * Components are pure data containers that define what an entity is.
 * 
 * @author Arriety
 */
@Setter
@Getter
public abstract class Component {
    
    /**
     * Unique identifier for the component type.
     * Used internally by the ECS framework for efficient component management.
     * -- GETTER --
     *  Gets the component type.
     *
     *
     * -- SETTER --
     *  Sets the component type. Used internally by the framework.
     *
     @return the component type
      * @param type the component type

     */
    private ComponentType type;
    
    /**
     * Reference to the entity that owns this component.
     * -- GETTER --
     *  Gets the entity that owns this component.
     *
     *
     * -- SETTER --
     *  Sets the owning entity. Used internally by the framework.
     *
     @return the owning entity
      * @param entity the owning entity

     */
    private Entity entity;
    
    /**
     * Whether this component is currently active.
     * Inactive components are ignored by systems.
     * -- GETTER --
     *  Checks if this component is active.
     *
     *
     * -- SETTER --
     *  Sets the active state of this component.
     *
     @return true if active, false otherwise
      * @param active the new active state

     */
    private boolean active = true;

    /**
     * Called when the component is added to an entity.
     * Override to perform initialization logic.
     */
    public void onAdded() {
        // Default implementation does nothing
    }
    
    /**
     * Called when the component is removed from an entity.
     * Override to perform cleanup logic.
     */
    public void onRemoved() {
        // Default implementation does nothing
    }
    
    /**
     * Resets the component to its default state.
     * Override to implement component pooling.
     */
    public void reset() {
        active = true;
        entity = null;
        type = null;
    }
} 