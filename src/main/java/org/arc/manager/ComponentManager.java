package org.arc.manager;

import org.arc.Component;
import org.arc.ComponentType;
import org.arc.ComponentTypeFactory;
import org.arc.Entity;
import org.arc.utils.Bag;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages component storage and retrieval for entities in the ECS framework.
 * Provides efficient access to components by type and entity.
 * 
 * @author Arriety
 */
public class ComponentManager {
    
    private final ConcurrentHashMap<ComponentType, Bag<Component>> componentsByType;
    private final Bag<Bag<Component>> componentsByEntity;
    
    /**
     * Creates a new component manager.
     */
    public ComponentManager() {
        componentsByType = new ConcurrentHashMap<>();
        componentsByEntity = new Bag<>();
    }
    
    /**
     * Adds a component to an entity.
     * @param entity the entity to add the component to
     * @param component the component to add
     */
    public void addComponent(Entity entity, Component component) {
        ComponentType type = component.getType();
        int entityId = entity.getId();
        
        // Add to type-based storage
        Bag<Component> componentsOfType = componentsByType.computeIfAbsent(
            type, k -> new Bag<>()
        );
        
        // Ensure the bag can hold this entity's component
        componentsOfType.ensureCapacity(entityId + 1);
        componentsOfType.set(entityId, component);
        
        // Add to entity-based storage
        componentsByEntity.ensureCapacity(entityId + 1);
        Bag<Component> entityComponents = componentsByEntity.get(entityId);
        if (entityComponents == null) {
            entityComponents = new Bag<>();
            componentsByEntity.set(entityId, entityComponents);
        }
        entityComponents.add(component);
    }
    
    /**
     * Removes a component from an entity.
     * @param entity the entity to remove the component from
     * @param component the component to remove
     */
    public void removeComponent(Entity entity, Component component) {
        ComponentType type = component.getType();
        int entityId = entity.getId();
        
        // Remove from type-based storage
        Bag<Component> componentsOfType = componentsByType.get(type);
        if (componentsOfType != null) {
            componentsOfType.set(entityId, null);
        }
        
        // Remove from entity-based storage
        Bag<Component> entityComponents = componentsByEntity.get(entityId);
        if (entityComponents != null) {
            entityComponents.remove(component);
        }
    }
    
    /**
     * Gets a component from an entity by type.
     * @param entity the entity to get the component from
     * @param componentClass the class of the component to get
     * @return the component, or null if not found
     */
    public <T extends Component> T getComponent(Entity entity, Class<T> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return getComponent(entity, type);
    }
    
    /**
     * Gets a component from an entity by type.
     * @param entity the entity to get the component from
     * @param type the type of the component to get
     * @return the component, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Entity entity, ComponentType type) {
        Bag<Component> componentsOfType = componentsByType.get(type);
        if (componentsOfType == null) {
            return null;
        }
        return (T) componentsOfType.get(entity.getId());
    }
    
    /**
     * Gets all components of a specific type.
     * @param componentClass the component class
     * @return a bag containing all components of the specified type
     */
    public <T extends Component> Bag<T> getComponentsOfType(Class<T> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return getComponentsOfType(type);
    }
    
    /**
     * Gets all components of a specific type.
     * @param type the component type
     * @return a bag containing all components of the specified type
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> Bag<T> getComponentsOfType(ComponentType type) {
        Bag<Component> components = componentsByType.get(type);
        if (components == null) {
            return new Bag<>();
        }
        
        Bag<T> result = new Bag<>();
        for (Component component : components) {
            if (component != null && component.isActive()) {
                result.add((T) component);
            }
        }
        return result;
    }
    
    /**
     * Gets all components attached to an entity.
     * @param entity the entity
     * @return a bag containing all components on the entity
     */
    public Bag<Component> getComponents(Entity entity) {
        Bag<Component> entityComponents = componentsByEntity.get(entity.getId());
        return entityComponents != null ? entityComponents : new Bag<>();
    }
    
    /**
     * Checks if an entity has a component of the specified type.
     * @param entity the entity to check
     * @param componentClass the component class to check for
     * @return true if the entity has the component, false otherwise
     */
    public boolean hasComponent(Entity entity, Class<? extends Component> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        return hasComponent(entity, type);
    }
    
    /**
     * Checks if an entity has a component of the specified type.
     * @param entity the entity to check
     * @param type the component type to check for
     * @return true if the entity has the component, false otherwise
     */
    public boolean hasComponent(Entity entity, ComponentType type) {
        Bag<Component> componentsOfType = componentsByType.get(type);
        if (componentsOfType == null) {
            return false;
        }
        Component component = componentsOfType.get(entity.getId());
        return component != null && component.isActive();
    }
    
    /**
     * Removes all components from an entity.
     * @param entity the entity to remove components from
     */
    public void removeAllComponents(Entity entity) {
        int entityId = entity.getId();
        
        // Remove from type-based storage
        for (Bag<Component> components : componentsByType.values()) {
            components.set(entityId, null);
        }
        
        // Clear entity-based storage
        Bag<Component> entityComponents = componentsByEntity.get(entityId);
        if (entityComponents != null) {
            entityComponents.clear();
        }
    }
    
    /**
     * Gets the number of component types currently registered.
     * @return the number of component types
     */
    public int getComponentTypeCount() {
        return componentsByType.size();
    }
    
    /**
     * Gets the total number of component instances.
     * @return the total component count
     */
    public int getTotalComponentCount() {
        int count = 0;
        for (Bag<Component> components : componentsByType.values()) {
            for (Component component : components) {
                if (component != null && component.isActive()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * Clears all components and resets the manager state.
     */
    public void clear() {
        componentsByType.clear();
        componentsByEntity.clear();
    }
    
    /**
     * Gets statistics about the component manager.
     * @return a string containing component manager statistics
     */
    public String getStatistics() {
        return String.format(
            "ComponentManager{types=%d, totalComponents=%d}",
            getComponentTypeCount(), getTotalComponentCount()
        );
    }
    
    @Override
    public String toString() {
        return getStatistics();
    }
} 