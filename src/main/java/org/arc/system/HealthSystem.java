package org.arc.system;

import org.arc.BaseSystem;
import org.arc.Entity;
import org.arc.component.Health;
import org.arc.utils.Bag;

/**
 * HealthSystem processes entities with Health components.
 * Handles health regeneration and death state management.
 * 
 * @author Arriety
 */
public class HealthSystem extends BaseSystem {
    
    public HealthSystem() {
        require(Health.class);
    }
    
    @Override
    protected void onUpdate(float deltaTime) {
        Bag<Entity> entities = world.getAllEntities();
        
        for (Entity entity : entities) {
            if (!matches(entity)) {
                continue;
            }
            
            Health health = entity.getComponent(Health.class);
            if (health != null) {
                updateHealth(entity, health, deltaTime);
            }
        }
    }
    
    /**
     * Updates health for a single entity.
     * @param entity the entity
     * @param health the health component
     * @param deltaTime the time delta
     */
    private void updateHealth(Entity entity, Health health, float deltaTime) {
        // Update health regeneration
        health.updateRegen(deltaTime);
        
        // Handle death state
        if (health.isDead()) {
            onEntityDeath(entity, health);
        }
    }
    
    /**
     * Called when an entity dies.
     * Override in subclasses for custom death handling.
     * @param entity the dead entity
     * @param health the health component
     */
    protected void onEntityDeath(Entity entity, Health health) {
        // Default implementation: could trigger death animations,
        // drop items, award experience, etc.
        // For now, we just mark the entity for removal after a delay
    }
} 