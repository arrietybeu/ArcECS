package org.arc.manager;

import org.arc.Entity;
import org.arc.World;
import org.arc.utils.Bag;
import org.arc.utils.IntDeque;

import java.util.BitSet;

/**
 * Manages entity creation, deletion, and lifecycle in the ECS framework.
 * Handles entity ID allocation and recycling for optimal performance.
 * 
 * @author Arriety
 */
public class EntityManager {
    
    private final Bag<Entity> entities;
    private final IntDeque recycledIds;
    private final BitSet activeEntityIds;
    private int nextId = 0;
    private int activeEntityCount = 0;
    
    /**
     * Creates a new entity manager.
     */
    public EntityManager() {
        entities = new Bag<>();
        recycledIds = new IntDeque();
        activeEntityIds = new BitSet();
    }
    
    /**
     * Creates a new entity with a unique ID.
     * @param world the world the entity belongs to
     * @return the created entity
     */
    public Entity createEntity(World world) {
        int id = obtainEntityId();
        Entity entity = createEntityInstance(world, id);
        
        entities.ensureCapacity(id + 1);
        entities.set(id, entity);
        activeEntityIds.set(id);
        activeEntityCount++;
        
        return entity;
    }
    
    /**
     * Creates an entity instance. Package-private to access Entity constructor.
     * @param world the world
     * @param id the entity ID
     * @return the entity instance
     */
    Entity createEntityInstance(World world, int id) {
        return new Entity(world, id);
    }
    
    /**
     * Deletes an entity and recycles its ID.
     * @param entity the entity to delete
     */
    public void deleteEntity(Entity entity) {
        if (entity == null || entity.isDeleted()) {
            return;
        }
        
        int id = entity.getId();
        entity.setDeleted(true);
        
        entities.set(id, null);
        activeEntityIds.clear(id);
        activeEntityCount--;
        
        recycledIds.addLast(id);
    }
    
    /**
     * Gets an entity by its ID.
     * @param id the entity ID
     * @return the entity, or null if not found or deleted
     */
    public Entity getEntity(int id) {
        Entity entity = entities.get(id);
        return (entity != null && !entity.isDeleted()) ? entity : null;
    }
    
    /**
     * Checks if an entity with the given ID is active.
     * @param id the entity ID
     * @return true if the entity is active, false otherwise
     */
    public boolean isActive(int id) {
        return activeEntityIds.get(id);
    }
    
    /**
     * Gets all active entities.
     * @return a bag containing all active entities
     */
    public Bag<Entity> getAllEntities() {
        Bag<Entity> activeEntities = new Bag<>();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity != null && !entity.isDeleted()) {
                activeEntities.add(entity);
            }
        }
        return activeEntities;
    }
    
    /**
     * Gets the number of active entities.
     * @return the active entity count
     */
    public int getActiveEntityCount() {
        return activeEntityCount;
    }
    
    /**
     * Gets the total number of entities ever created (including deleted ones).
     * @return the total entity count
     */
    public int getTotalEntityCount() {
        return nextId;
    }
    
    /**
     * Gets the number of recycled entity IDs.
     * @return the recycled ID count
     */
    public int getRecycledIdCount() {
        return recycledIds.size();
    }
    
    /**
     * Clears all entities and resets the manager state.
     */
    public void clear() {
        entities.clear();
        recycledIds.clear();
        activeEntityIds.clear();
        nextId = 0;
        activeEntityCount = 0;
    }
    
    /**
     * Obtains an entity ID, either by recycling an old one or creating a new one.
     * @return an entity ID
     */
    private int obtainEntityId() {
        if (!recycledIds.isEmpty()) {
            return recycledIds.removeFirst();
        }
        return nextId++;
    }
    
    /**
     * Gets statistics about the entity manager.
     * @return a string containing entity manager statistics
     */
    public String getStatistics() {
        return String.format(
            "EntityManager{active=%d, total=%d, recycled=%d, capacity=%d}",
            activeEntityCount, nextId, recycledIds.size(), entities.capacity()
        );
    }
    
    @Override
    public String toString() {
        return getStatistics();
    }
} 