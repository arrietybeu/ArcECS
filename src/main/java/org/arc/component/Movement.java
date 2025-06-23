package org.arc.component;

import org.arc.Component;

/**
 * Movement component for entities that can move around the game world.
 * Handles velocity, acceleration, and movement constraints.
 * 
 * @author Arriety
 */
public class Movement extends Component {
    
    public float velocityX = 0f;
    public float velocityY = 0f;
    public float accelerationX = 0f;
    public float accelerationY = 0f;
    
    public float maxSpeed = 100f; // Units per second
    public float friction = 0.8f; // Friction coefficient (0-1)
    public boolean gravityEnabled = false;
    public float gravityScale = 1f;
    
    // Movement state
    public boolean canMove = true;
    public boolean grounded = false;
    public float groundCheckOffset = 0.1f;
    
    /**
     * Creates a movement component with default settings.
     */
    public Movement() {
    }
    
    /**
     * Creates a movement component with the specified max speed.
     * @param maxSpeed the maximum speed
     */
    public Movement(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    
    /**
     * Creates a movement component with max speed and friction.
     * @param maxSpeed the maximum speed
     * @param friction the friction coefficient
     */
    public Movement(float maxSpeed, float friction) {
        this.maxSpeed = maxSpeed;
        this.friction = Math.max(0f, Math.min(1f, friction));
    }
    
    /**
     * Sets the velocity.
     * @param velocityX the X velocity
     * @param velocityY the Y velocity
     */
    public void setVelocity(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    
    /**
     * Adds to the current velocity.
     * @param deltaVelocityX the X velocity delta
     * @param deltaVelocityY the Y velocity delta
     */
    public void addVelocity(float deltaVelocityX, float deltaVelocityY) {
        this.velocityX += deltaVelocityX;
        this.velocityY += deltaVelocityY;
    }
    
    /**
     * Sets the acceleration.
     * @param accelerationX the X acceleration
     * @param accelerationY the Y acceleration
     */
    public void setAcceleration(float accelerationX, float accelerationY) {
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }
    
    /**
     * Gets the current speed (magnitude of velocity).
     * @return the current speed
     */
    public float getSpeed() {
        return (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
    }
    
    /**
     * Gets the maximum speed.
     * @return the maximum speed
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }
    
    /**
     * Sets the maximum speed.
     * @param maxSpeed the maximum speed
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = Math.max(0, maxSpeed);
    }
    
    /**
     * Checks if the entity can move.
     * @return true if movement is allowed, false otherwise
     */
    public boolean canMove() {
        return canMove;
    }
    
    /**
     * Sets whether the entity can move.
     * @param canMove the movement permission
     */
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
        if (!canMove) {
            // Stop movement when disabled
            velocityX = 0f;
            velocityY = 0f;
            accelerationX = 0f;
            accelerationY = 0f;
        }
    }
    
    /**
     * Checks if the entity is on the ground.
     * @return true if grounded, false otherwise
     */
    public boolean isGrounded() {
        return grounded;
    }
    
    /**
     * Sets the grounded state.
     * @param grounded the grounded state
     */
    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }
    
    /**
     * Applies friction to the velocity.
     * @param deltaTime the time delta
     */
    public void applyFriction(float deltaTime) {
        if (friction > 0) {
            float frictionMultiplier = Math.max(0f, 1f - friction * deltaTime * 10f);
            velocityX *= frictionMultiplier;
            velocityY *= frictionMultiplier;
        }
    }
    
    /**
     * Applies gravity to the velocity.
     * @param gravity the gravity force
     * @param deltaTime the time delta
     */
    public void applyGravity(float gravity, float deltaTime) {
        if (gravityEnabled && !grounded) {
            velocityY += gravity * gravityScale * deltaTime;
        }
    }
    
    /**
     * Clamps the velocity to the maximum speed.
     */
    public void clampVelocity() {
        float speed = getSpeed();
        if (speed > maxSpeed) {
            float ratio = maxSpeed / speed;
            velocityX *= ratio;
            velocityY *= ratio;
        }
    }
    
    /**
     * Stops all movement immediately.
     */
    public void stop() {
        velocityX = 0f;
        velocityY = 0f;
        accelerationX = 0f;
        accelerationY = 0f;
    }
    
    /**
     * Makes the entity jump with the specified force.
     * @param jumpForce the jump force
     */
    public void jump(float jumpForce) {
        if (grounded && canMove) {
            velocityY = -jumpForce; // Negative Y is up in many 2D coordinate systems
            grounded = false;
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        velocityX = 0f;
        velocityY = 0f;
        accelerationX = 0f;
        accelerationY = 0f;
        maxSpeed = 100f;
        friction = 0.8f;
        gravityEnabled = false;
        gravityScale = 1f;
        canMove = true;
        grounded = false;
        groundCheckOffset = 0.1f;
    }
    
    @Override
    public String toString() {
        return String.format("Movement{vel=(%.1f, %.1f), accel=(%.1f, %.1f), speed=%.1f/%.1f, grounded=%b}", 
                velocityX, velocityY, accelerationX, accelerationY, getSpeed(), maxSpeed, grounded);
    }
} 