package org.arc;

import org.arc.utils.Bag;
import org.arc.utils.BitVector;

/**
 * Represents an entity in the ECS framework.
 * Entities are containers for components and have a unique ID.
 * 
 * @author Arriety
 */
public class Entity {
    
    private final int id;
    private World world;
    private final Bag<Component> components;
    private final BitVector componentBits;
    private boolean deleted = false;
    
    /**
     * Creates a new entity.
     * @param world the world this entity belongs to
     * @param id the unique entity ID
     */
    public Entity(World world, int id) {
        this.world = world;
        this.id = id;
        this.components = new Bag<>();
        this.componentBits = new BitVector();
    }
    
    /**
     * Sets the world reference. Used internally by the framework.
     * @param world the world
     */
    void setWorld(World world) {
        this.world = world;
    }
    
    /**
     * Gets the entity's unique ID.
     * @return the entity ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the world this entity belongs to.
     * @return the world
     */
    public World getWorld() {
        return world;
    }
    
    /**
     * Checks if this entity has been deleted.
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Marks this entity as deleted. Used internally by the framework.
     * @param deleted the deletion state
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    /**
     * Adds a component to this entity.
     * @param component the component to add
     * @return this entity for method chaining
     */
    public Entity addComponent(Component component) {
        if (deleted) {
            throw new IllegalStateException("Cannot add component to deleted entity");
        }
        
        ComponentType type = ComponentTypeFactory.getTypeFor(component.getClass());
        component.setType(type);
        component.setEntity(this);
        
        // Ensure components bag is large enough
        components.ensureCapacity(type.getIndex() + 1);
        components.set(type.getIndex(), component);
        componentBits.set(type.getIndex());
        
        component.onAdded();
        world.getComponentManager().addComponent(this, component);
        
        return this;
    }
    
    /**
     * Removes a component from this entity.
     * @param componentClass the class of the component to remove
     * @return the removed component, or null if not found
     */
    public <T extends Component> T removeComponent(Class<T> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return removeComponent(type);
    }
    
    /**
     * Removes a component from this entity.
     * @param type the type of the component to remove
     * @return the removed component, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T removeComponent(ComponentType type) {
        if (deleted || !componentBits.get(type.getIndex())) {
            return null;
        }
        
        T component = (T) components.get(type.getIndex());
        if (component != null) {
            components.set(type.getIndex(), null);
            componentBits.clear(type.getIndex());
            
            component.onRemoved();
            world.getComponentManager().removeComponent(this, component);
        }
        
        return component;
    }
    
    /**
     * Gets a component from this entity.
     * @param componentClass the class of the component to get
     * @return the component, or null if not found
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return getComponent(type);
    }
    
    /**
     * Gets a component from this entity.
     * @param type the type of the component to get
     * @return the component, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(ComponentType type) {
        if (deleted || !componentBits.get(type.getIndex())) {
            return null;
        }
        return (T) components.get(type.getIndex());
    }
    
    /**
     * Checks if this entity has a component of the given type.
     * @param componentClass the component class to check for
     * @return true if the component exists, false otherwise
     */
    public boolean hasComponent(Class<? extends Component> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return hasComponent(type);
    }
    
    /**
     * Checks if this entity has a component of the given type.
     * @param type the component type to check for
     * @return true if the component exists, false otherwise
     */
    public boolean hasComponent(ComponentType type) {
        return !deleted && componentBits.get(type.getIndex());
    }
    
    /**
     * Gets the component bits for this entity.
     * Used internally by systems for efficient entity matching.
     * @return the component bits
     */
    public BitVector getComponentBits() {
        return componentBits;
    }
    
    /**
     * Gets all components on this entity.
     * @return a bag containing all components
     */
    public Bag<Component> getComponents() {
        return components;
    }
    
    /**
     * Deletes this entity from the world.
     */
    public void delete() {
        world.deleteEntity(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity entity = (Entity) obj;
        return id == entity.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Entity{id=" + id + ", components=" + componentBits.cardinality() + "}";
    }
} 