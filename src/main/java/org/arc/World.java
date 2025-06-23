package org.arc;

import lombok.Getter;
import org.arc.manager.ComponentManager;
import org.arc.manager.EntityManager;
import org.arc.utils.Bag;

/**
 * The main container and orchestrator for the ECS framework.
 * Manages entities, components, and systems, and provides the main update loop.
 * 
 * @author Arriety
 */
@Getter
public class World {

    /**
     * -- GETTER --
     *  Gets the world configuration.
     *
     * @return the world configuration
     */
    private final WorldConfiguration configuration;
    /**
     * -- GETTER --
     *  Gets the entity manager.
     *
     * @return the entity manager
     */
    private final EntityManager entityManager;
    /**
     * -- GETTER --
     *  Gets the component manager.
     *
     * @return the component manager
     */
    private final ComponentManager componentManager;
    /**
     * -- GETTER --
     *  Gets all systems in this world.
     *
     * @return a bag containing all systems
     */
    private final Bag<BaseSystem> systems;

    /**
     * -- GETTER --
     *  Checks if the world has been initialized.
     *
     * @return true if initialized, false otherwise
     */
    private boolean initialized = false;
    /**
     * -- GETTER --
     *  Gets the current delta time.
     *
     * @return the delta time in seconds
     */
    private float delta = 0f;
    
    /**
     * Creates a new world with default configuration.
     */
    public World() {
        this(new WorldConfiguration());
    }
    
    /**
     * Creates a new world with the specified configuration.
     * @param configuration the world configuration
     */
    public World(WorldConfiguration configuration) {
        this.configuration = configuration;
        this.entityManager = new EntityManager();
        this.componentManager = new ComponentManager();
        this.systems = new Bag<>();
    }
    
    /**
     * Creates a new entity in this world.
     * @return the created entity
     */
    public Entity createEntity() {
        return entityManager.createEntity(this);
    }
    
    /**
     * Deletes an entity from this world.
     * @param entity the entity to delete
     */
    public void deleteEntity(Entity entity) {
        if (entity == null || entity.isDeleted()) {
            return;
        }
        
        // Remove all components from the entity
        componentManager.removeAllComponents(entity);
        
        // Delete the entity
        entityManager.deleteEntity(entity);
        
        // Notify systems about entity deletion
        for (BaseSystem system : systems) {
            if (system.isEnabled()) {
                system.onEntityDeleted(entity);
            }
        }
    }
    
    /**
     * Gets an entity by its ID.
     * @param id the entity ID
     * @return the entity, or null if not found
     */
    public Entity getEntity(int id) {
        return entityManager.getEntity(id);
    }
    
    /**
     * Gets all entities in this world.
     * @return a bag containing all active entities
     */
    public Bag<Entity> getAllEntities() {
        return entityManager.getAllEntities();
    }
    
    /**
     * Adds a system to this world.
     * @param system the system to add
     * @return this world for method chaining
     */
    public World addSystem(BaseSystem system) {
        system.setWorld(this);
        systems.add(system);
        
        if (initialized) {
            system.initialize();
        }
        
        return this;
    }
    
    /**
     * Removes a system from this world.
     * @param system the system to remove
     * @return true if the system was removed, false otherwise
     */
    public boolean removeSystem(BaseSystem system) {
        if (systems.remove(system)) {
            system.dispose();
            return true;
        }
        return false;
    }
    
    /**
     * Gets a system by its class.
     * @param systemClass the system class
     * @return the system, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseSystem> T getSystem(Class<T> systemClass) {
        for (BaseSystem system : systems) {
            if (systemClass.isInstance(system)) {
                return (T) system;
            }
        }
        return null;
    }

    /**
     * Initializes the world and all its systems.
     * Must be called before the first update.
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        
        // Initialize all systems
        for (BaseSystem system : systems) {
            system.initialize();
        }
        
        initialized = true;
    }
    
    /**
     * Updates the world and all its systems.
     * @param deltaTime the time elapsed since the last update in seconds
     */
    public void update(float deltaTime) {
        if (!initialized) {
            throw new IllegalStateException("World must be initialized before updating");
        }
        
        this.delta = deltaTime;
        
        // Update all enabled systems
        for (BaseSystem system : systems) {
            if (system.isEnabled()) {
                system.update(deltaTime);
            }
        }
    }
    
    /**
     * Disposes of the world and all its resources.
     * Should be called when the world is no longer needed.
     */
    public void dispose() {
        // Dispose all systems
        for (BaseSystem system : systems) {
            system.dispose();
        }
        systems.clear();
        
        // Clear managers
        componentManager.clear();
        entityManager.clear();
        
        initialized = false;
    }

    /**
     * Gets the number of active entities.
     * @return the active entity count
     */
    public int getEntityCount() {
        return entityManager.getActiveEntityCount();
    }
    
    /**
     * Gets the number of systems.
     * @return the system count
     */
    public int getSystemCount() {
        return systems.size();
    }
    
    /**
     * Gets statistics about the world.
     * @return a string containing world statistics
     */
    public String getStatistics() {
        return String.format(
            "World{entities=%d, systems=%d, initialized=%b, %s, %s}",
            getEntityCount(), getSystemCount(), initialized,
            entityManager.getStatistics(), componentManager.getStatistics()
        );
    }
    
    @Override
    public String toString() {
        return getStatistics();
    }
} 