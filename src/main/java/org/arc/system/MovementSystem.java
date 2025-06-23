package org.arc.system;

import org.arc.BaseSystem;
import org.arc.Entity;
import org.arc.component.Movement;
import org.arc.component.Transform;
import org.arc.utils.Bag;

/**
 * MovementSystem processes entities with Transform and Movement components.
 * Handles position updates, velocity application, friction, and gravity.
 * 
 * @author Arriety
 */
public class MovementSystem extends BaseSystem {
    
    private static final float GRAVITY = 980f; // Default gravity in units/second²
    
    public MovementSystem() {
        require(Transform.class);
        require(Movement.class);
    }
    
    @Override
    protected void onUpdate(float deltaTime) {
        Bag<Entity> entities = world.getAllEntities();
        
        for (Entity entity : entities) {
            if (!matches(entity)) {
                continue;
            }
            
            Transform transform = entity.getComponent(Transform.class);
            Movement movement = entity.getComponent(Movement.class);
            
            if (transform != null && movement != null && movement.canMove()) {
                updateMovement(transform, movement, deltaTime);
            }
        }
    }
    
    /**
     * Updates movement for a single entity.
     * @param transform the transform component
     * @param movement the movement component
     * @param deltaTime the time delta
     */
    private void updateMovement(Transform transform, Movement movement, float deltaTime) {
        // Apply gravity if enabled
        if (movement.gravityEnabled) {
            movement.applyGravity(GRAVITY, deltaTime);
        }
        
        // Apply acceleration to velocity
        movement.velocityX += movement.accelerationX * deltaTime;
        movement.velocityY += movement.accelerationY * deltaTime;
        
        // Apply friction
        movement.applyFriction(deltaTime);
        
        // Clamp velocity to max speed
        movement.clampVelocity();
        
        // Update position based on velocity
        transform.translate(movement.velocityX * deltaTime, movement.velocityY * deltaTime);
        
        // Basic ground collision (simple y=0 ground)
        if (movement.gravityEnabled && transform.y >= 0) {
            if (movement.velocityY > 0) {
                transform.y = 0;
                movement.velocityY = 0;
                movement.setGrounded(true);
            }
        } else {
            movement.setGrounded(false);
        }
    }
} 