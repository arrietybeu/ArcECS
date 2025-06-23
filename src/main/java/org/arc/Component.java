package org.arc;

/**
 * Base class for all components in the ECS framework.
 * Components are pure data containers that define what an entity is.
 * 
 * @author Arriety
 */
public abstract class Component {
    
    /**
     * Unique identifier for the component type.
     * Used internally by the ECS framework for efficient component management.
     */
    private ComponentType type;
    
    /**
     * Reference to the entity that owns this component.
     */
    private Entity entity;
    
    /**
     * Whether this component is currently active.
     * Inactive components are ignored by systems.
     */
    private boolean active = true;
    
    /**
     * Gets the component type.
     * @return the component type
     */
    public ComponentType getType() {
        return type;
    }
    
    /**
     * Sets the component type. Used internally by the framework.
     * @param type the component type
     */
    public void setType(ComponentType type) {
        this.type = type;
    }
    
    /**
     * Gets the entity that owns this component.
     * @return the owning entity
     */
    public Entity getEntity() {
        return entity;
    }
    
    /**
     * Sets the owning entity. Used internally by the framework.
     * @param entity the owning entity
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    /**
     * Checks if this component is active.
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets the active state of this component.
     * @param active the new active state
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
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