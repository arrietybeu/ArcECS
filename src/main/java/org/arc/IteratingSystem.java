package org.arc;

import org.arc.utils.Bag;

/**
 * An abstract system that automatically iterates over all matching entities
 * and calls the process method for each entity during updates.
 * 
 * Subclasses should implement the process method to define their logic
 * and use the require/exclude methods to specify which entities to process.
 * 
 * @author Arriety
 */
public abstract class IteratingSystem extends BaseSystem {
    
    /**
     * Creates a new iterating system.
     */
    public IteratingSystem() {
        super();
    }
    
    /**
     * Updates the system by iterating over all matching entities
     * and calling the process method for each one.
     * 
     * @param deltaTime the time elapsed since the last update in seconds
     */
    @Override
    protected final void onUpdate(float deltaTime) {
        if (world == null) {
            return;
        }
        
        // Get all entities from the world
        Bag<Entity> entities = world.getEntityManager().getAllEntities();
        
        // Iterate through all entities and process matching ones
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            
            // Skip null, deleted entities, or entities that don't match our requirements
            if (entity == null || entity.isDeleted() || !matches(entity)) {
                continue;
            }
            
            // Process the matching entity
            process(entity, deltaTime);
        }
    }
    
    /**
     * Processes a single entity.
     * This method is called once per frame for each entity that matches
     * the system's component requirements and exclusions.
     * 
     * @param entity the entity to process
     * @param deltaTime the time elapsed since the last update in seconds
     */
    protected abstract void process(Entity entity, float deltaTime);
} 