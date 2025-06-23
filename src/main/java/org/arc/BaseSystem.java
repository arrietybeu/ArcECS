package org.arc;

import org.arc.utils.BitVector;

/**
 * Base class for all systems in the ECS framework.
 * Systems contain the logic that operates on entities with specific component combinations.
 * 
 * @author Arriety
 */
public abstract class BaseSystem {
    
    protected World world;
    private boolean enabled = true;
    private boolean initialized = false;
    private final BitVector componentRequirements;
    private final BitVector componentExclusions;
    
    /**
     * Creates a new base system.
     */
    public BaseSystem() {
        componentRequirements = new BitVector();
        componentExclusions = new BitVector();
    }
    
    /**
     * Sets the world this system belongs to.
     * Called automatically when the system is added to a world.
     * @param world the world
     */
    public void setWorld(World world) {
        this.world = world;
    }
    
    /**
     * Gets the world this system belongs to.
     * @return the world
     */
    public World getWorld() {
        return world;
    }
    
    /**
     * Checks if this system is enabled.
     * Disabled systems are not updated.
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Enables or disables this system.
     * @param enabled the enabled state
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Checks if this system has been initialized.
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Initializes the system.
     * Called automatically when the system is added to an initialized world.
     * Override to perform initialization logic.
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        
        onInitialize();
        initialized = true;
    }
    
    /**
     * Updates the system.
     * Called every frame for enabled systems.
     * @param deltaTime the time elapsed since the last update in seconds
     */
    public void update(float deltaTime) {
        if (!enabled || !initialized) {
            return;
        }
        
        onUpdate(deltaTime);
    }
    
    /**
     * Disposes of the system and its resources.
     * Called when the system is removed from the world or the world is disposed.
     */
    public void dispose() {
        if (!initialized) {
            return;
        }
        
        onDispose();
        initialized = false;
    }
    
    /**
     * Called when an entity is deleted from the world.
     * Override to perform cleanup for entity-specific data.
     * @param entity the deleted entity
     */
    public void onEntityDeleted(Entity entity) {
        // Default implementation does nothing
    }
    
    /**
     * Requires entities to have a component of the specified type.
     * @param componentClass the component class to require
     * @return this system for method chaining
     */
    protected BaseSystem require(Class<? extends Component> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        componentRequirements.set(type.getIndex());
        return this;
    }
    
    /**
     * Excludes entities that have a component of the specified type.
     * @param componentClass the component class to exclude
     * @return this system for method chaining
     */
    protected BaseSystem exclude(Class<? extends Component> componentClass) {
        ComponentType type = ComponentTypeFactory.getTypeFor(componentClass);
        componentExclusions.set(type.getIndex());
        return this;
    }
    
    /**
     * Checks if an entity matches this system's component requirements.
     * @param entity the entity to check
     * @return true if the entity matches, false otherwise
     */
    protected boolean matches(Entity entity) {
        BitVector entityBits = entity.getComponentBits();
        
        // Check if entity has all required components
        if (!entityBits.containsAll(componentRequirements)) {
            return false;
        }
        
        // Check if entity has none of the excluded components
        if (entityBits.intersects(componentExclusions)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets a component from an entity.
     * Convenience method that delegates to the entity.
     * @param entity the entity
     * @param componentClass the component class
     * @return the component, or null if not found
     */
    protected <T extends Component> T getComponent(Entity entity, Class<T> componentClass) {
        return entity.getComponent(componentClass);
    }
    
    /**
     * Called during system initialization.
     * Override to perform custom initialization logic.
     */
    protected void onInitialize() {
        // Default implementation does nothing
    }
    
    /**
     * Called during system update.
     * Override to implement system logic.
     * @param deltaTime the time elapsed since the last update in seconds
     */
    protected abstract void onUpdate(float deltaTime);
    
    /**
     * Called during system disposal.
     * Override to perform custom cleanup logic.
     */
    protected void onDispose() {
        // Default implementation does nothing
    }
    
    @Override
    public String toString() {
        return String.format(
            "%s{enabled=%b, initialized=%b, requirements=%d, exclusions=%d}",
            getClass().getSimpleName(), enabled, initialized,
            componentRequirements.cardinality(), componentExclusions.cardinality()
        );
    }
} 