package org.arc.component;

import org.arc.Component;

/**
 * Health component for entities that can take damage and be killed.
 * Used by players, monsters, bosses, and some NPCs.
 * 
 * @author Arriety
 */
public class Health extends Component {
    
    private float currentHealth;
    private float maxHealth;
    private boolean isDead = false;
    private boolean invulnerable = false;
    private float healthRegenRate = 0f; // Health per second
    private float lastDamageTime = 0f;
    
    /**
     * Creates a health component with the specified max health.
     * @param maxHealth the maximum health
     */
    public Health(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }
    
    /**
     * Gets the current health.
     * @return the current health
     */
    public float getCurrentHealth() {
        return currentHealth;
    }
    
    /**
     * Gets the maximum health.
     * @return the maximum health
     */
    public float getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Sets the maximum health and adjusts current health if necessary.
     * @param maxHealth the new maximum health
     */
    public void setMaxHealth(float maxHealth) {
        this.maxHealth = Math.max(0, maxHealth);
        this.currentHealth = Math.min(this.currentHealth, this.maxHealth);
    }
    
    /**
     * Gets the health percentage (0.0 to 1.0).
     * @return the health percentage
     */
    public float getHealthPercentage() {
        return maxHealth > 0 ? currentHealth / maxHealth : 0f;
    }
    
    /**
     * Checks if the entity is dead.
     * @return true if dead, false otherwise
     */
    public boolean isDead() {
        return isDead;
    }
    
    /**
     * Checks if the entity is invulnerable to damage.
     * @return true if invulnerable, false otherwise
     */
    public boolean isInvulnerable() {
        return invulnerable;
    }
    
    /**
     * Sets the invulnerability state.
     * @param invulnerable the invulnerability state
     */
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
    
    /**
     * Gets the health regeneration rate.
     * @return the health regeneration rate per second
     */
    public float getHealthRegenRate() {
        return healthRegenRate;
    }
    
    /**
     * Sets the health regeneration rate.
     * @param healthRegenRate the health regeneration rate per second
     */
    public void setHealthRegenRate(float healthRegenRate) {
        this.healthRegenRate = Math.max(0, healthRegenRate);
    }
    
    /**
     * Gets the time since the last damage was taken.
     * @param currentTime the current time
     * @return the time since last damage
     */
    public float getTimeSinceLastDamage(float currentTime) {
        return currentTime - lastDamageTime;
    }
    
    /**
     * Deals damage to this entity.
     * @param damage the damage amount
     * @param currentTime the current time
     * @return the actual damage dealt
     */
    public float takeDamage(float damage, float currentTime) {
        if (invulnerable || isDead || damage <= 0) {
            return 0f;
        }
        
        float actualDamage = Math.min(damage, currentHealth);
        currentHealth -= actualDamage;
        lastDamageTime = currentTime;
        
        if (currentHealth <= 0) {
            currentHealth = 0;
            isDead = true;
        }
        
        return actualDamage;
    }
    
    /**
     * Heals this entity.
     * @param healing the healing amount
     * @return the actual healing done
     */
    public float heal(float healing) {
        if (isDead || healing <= 0) {
            return 0f;
        }
        
        float actualHealing = Math.min(healing, maxHealth - currentHealth);
        currentHealth += actualHealing;
        
        return actualHealing;
    }
    
    /**
     * Restores the entity to full health and removes dead status.
     */
    public void fullHeal() {
        currentHealth = maxHealth;
        isDead = false;
    }
    
    /**
     * Instantly kills the entity.
     */
    public void kill() {
        currentHealth = 0;
        isDead = true;
    }
    
    /**
     * Revives the entity with the specified health.
     * @param health the health to revive with
     */
    public void revive(float health) {
        if (isDead) {
            currentHealth = Math.max(1, Math.min(health, maxHealth));
            isDead = false;
        }
    }
    
    /**
     * Updates health regeneration.
     * @param deltaTime the time elapsed since last update
     */
    public void updateRegen(float deltaTime) {
        if (!isDead && healthRegenRate > 0 && currentHealth < maxHealth) {
            heal(healthRegenRate * deltaTime);
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        currentHealth = maxHealth;
        isDead = false;
        invulnerable = false;
        healthRegenRate = 0f;
        lastDamageTime = 0f;
    }
    
    @Override
    public String toString() {
        return String.format("Health{%.1f/%.1f (%.1f%%), dead=%b, invuln=%b, regen=%.1f/s}", 
                currentHealth, maxHealth, getHealthPercentage() * 100f, isDead, invulnerable, healthRegenRate);
    }
} 