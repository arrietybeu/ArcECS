package org.arc.component;

import org.arc.Component;

/**
 * Transform component that defines position, rotation, and scale.
 * Essential for all entities that exist in the game world.
 * 
 * @author Arriety
 */
public class Transform extends Component {
    
    public float x = 0f;
    public float y = 0f;
    public float rotation = 0f; // In degrees
    public float scaleX = 1f;
    public float scaleY = 1f;
    
    /**
     * Creates a transform at origin with default scale.
     */
    public Transform() {
    }
    
    /**
     * Creates a transform at the specified position.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Transform(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Creates a transform with position and rotation.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rotation the rotation in degrees
     */
    public Transform(float x, float y, float rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }
    
    /**
     * Sets the position.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Moves the transform by the specified offset.
     * @param deltaX the x offset
     * @param deltaY the y offset
     */
    public void translate(float deltaX, float deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }
    
    /**
     * Sets the rotation.
     * @param rotation the rotation in degrees
     */
    public void setRotation(float rotation) {
        this.rotation = rotation % 360f;
    }
    
    /**
     * Rotates by the specified amount.
     * @param deltaRotation the rotation delta in degrees
     */
    public void rotate(float deltaRotation) {
        this.rotation = (this.rotation + deltaRotation) % 360f;
    }
    
    /**
     * Sets the scale uniformly.
     * @param scale the scale factor
     */
    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }
    
    /**
     * Sets the scale with separate X and Y factors.
     * @param scaleX the X scale factor
     * @param scaleY the Y scale factor
     */
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    /**
     * Calculates the distance to another transform.
     * @param other the other transform
     * @return the distance
     */
    public float distanceTo(Transform other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calculates the squared distance to another transform (faster than distance).
     * @param other the other transform
     * @return the squared distance
     */
    public float distanceSquaredTo(Transform other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return dx * dx + dy * dy;
    }
    
    @Override
    public void reset() {
        super.reset();
        x = 0f;
        y = 0f;
        rotation = 0f;
        scaleX = 1f;
        scaleY = 1f;
    }
    
    @Override
    public String toString() {
        return String.format("Transform{pos=(%.2f, %.2f), rot=%.1f°, scale=(%.2f, %.2f)}", 
                x, y, rotation, scaleX, scaleY);
    }
} 