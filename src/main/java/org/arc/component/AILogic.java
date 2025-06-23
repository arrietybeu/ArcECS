package org.arc.component;

import org.arc.Component;
import org.arc.Entity;

/**
 * AI Logic component for entities with artificial intelligence.
 * Manages AI states, behaviors, and decision-making for monsters and bosses.
 * 
 * @author Arriety
 */
public class AILogic extends Component {
    
    /**
     * Enumeration of AI states.
     */
    public enum AIState {
        IDLE,           // Standing around, not doing much
        PATROL,         // Moving along a predefined path
        CHASE,          // Pursuing a target
        ATTACK,         // Actively attacking a target
        RETREAT,        // Moving away from danger
        SEARCH,         // Looking for a lost target
        STUNNED,        // Temporarily unable to act
        DEAD            // Entity is dead
    }
    
    /**
     * Enumeration of AI behavior types.
     */
    public enum AIBehavior {
        PASSIVE,        // Never attacks first
        DEFENSIVE,      // Attacks when attacked
        AGGRESSIVE,     // Attacks nearby enemies
        TERRITORIAL,    // Attacks enemies in its territory
        BOSS           // Complex boss behavior
    }
    
    private AIState currentState = AIState.IDLE;
    private AIBehavior behavior = AIBehavior.PASSIVE;
    private Entity target = null;
    private float lastStateChange = 0f;
    private float stateTimer = 0f;
    
    // AI Parameters
    private float detectionRange = 100f;   // Range to detect enemies
    private float attackRange = 50f;       // Range to start attacking
    private float chaseRange = 200f;       // Range to continue chasing
    private float retreatThreshold = 0.2f; // Health percentage to retreat
    private float attackCooldown = 2f;     // Time between attacks
    private float lastAttackTime = 0f;
    
    // Patrol parameters
    private float[] patrolPoints = new float[0]; // X, Y pairs
    private int currentPatrolIndex = 0;
    private float patrolSpeed = 50f;
    
    // State-specific timers
    private float idleTimeout = 5f;        // Max time to stay idle
    private float searchTimeout = 10f;     // Max time to search for target
    private float stunDuration = 3f;       // Duration of stun effect
    
    /**
     * Creates an AI logic component with default settings.
     */
    public AILogic() {
    }
    
    /**
     * Creates an AI logic component with specified behavior.
     * @param behavior the AI behavior type
     */
    public AILogic(AIBehavior behavior) {
        this.behavior = behavior;
    }
    
    /**
     * Gets the current AI state.
     * @return the current state
     */
    public AIState getCurrentState() {
        return currentState;
    }
    
    /**
     * Sets the AI state.
     * @param newState the new state
     * @param currentTime the current time
     */
    public void setState(AIState newState, float currentTime) {
        if (currentState != newState) {
            onStateExit(currentState, currentTime);
            currentState = newState;
            lastStateChange = currentTime;
            stateTimer = 0f;
            onStateEnter(newState, currentTime);
        }
    }
    
    /**
     * Gets the AI behavior type.
     * @return the behavior type
     */
    public AIBehavior getBehavior() {
        return behavior;
    }
    
    /**
     * Sets the AI behavior type.
     * @param behavior the behavior type
     */
    public void setBehavior(AIBehavior behavior) {
        this.behavior = behavior;
    }
    
    /**
     * Gets the current target.
     * @return the target entity, or null if no target
     */
    public Entity getTarget() {
        return target;
    }
    
    /**
     * Sets the target entity.
     * @param target the target entity
     */
    public void setTarget(Entity target) {
        this.target = target;
    }
    
    /**
     * Gets the detection range.
     * @return the detection range
     */
    public float getDetectionRange() {
        return detectionRange;
    }
    
    /**
     * Sets the detection range.
     * @param detectionRange the detection range
     */
    public void setDetectionRange(float detectionRange) {
        this.detectionRange = Math.max(0, detectionRange);
    }
    
    /**
     * Gets the attack range.
     * @return the attack range
     */
    public float getAttackRange() {
        return attackRange;
    }
    
    /**
     * Sets the attack range.
     * @param attackRange the attack range
     */
    public void setAttackRange(float attackRange) {
        this.attackRange = Math.max(0, attackRange);
    }
    
    /**
     * Gets the chase range.
     * @return the chase range
     */
    public float getChaseRange() {
        return chaseRange;
    }
    
    /**
     * Sets the chase range.
     * @param chaseRange the chase range
     */
    public void setChaseRange(float chaseRange) {
        this.chaseRange = Math.max(0, chaseRange);
    }
    
    /**
     * Checks if the AI can attack.
     * @param currentTime the current time
     * @return true if can attack, false otherwise
     */
    public boolean canAttack(float currentTime) {
        return (currentTime - lastAttackTime) >= attackCooldown;
    }
    
    /**
     * Records that an attack was performed.
     * @param currentTime the current time
     */
    public void recordAttack(float currentTime) {
        lastAttackTime = currentTime;
    }
    
    /**
     * Gets the time in current state.
     * @param currentTime the current time
     * @return the time spent in current state
     */
    public float getTimeInState(float currentTime) {
        return currentTime - lastStateChange;
    }
    
    /**
     * Updates the state timer.
     * @param deltaTime the time delta
     */
    public void updateStateTimer(float deltaTime) {
        stateTimer += deltaTime;
    }
    
    /**
     * Gets the state timer.
     * @return the state timer
     */
    public float getStateTimer() {
        return stateTimer;
    }
    
    /**
     * Sets patrol points for patrol behavior.
     * @param points array of X, Y coordinate pairs
     */
    public void setPatrolPoints(float... points) {
        if (points.length % 2 != 0) {
            throw new IllegalArgumentException("Patrol points must be X,Y pairs");
        }
        this.patrolPoints = points.clone();
        this.currentPatrolIndex = 0;
    }
    
    /**
     * Gets the current patrol point.
     * @return array containing X, Y coordinates, or null if no patrol points
     */
    public float[] getCurrentPatrolPoint() {
        if (patrolPoints.length == 0) {
            return null;
        }
        int index = currentPatrolIndex * 2;
        return new float[]{patrolPoints[index], patrolPoints[index + 1]};
    }
    
    /**
     * Advances to the next patrol point.
     */
    public void nextPatrolPoint() {
        if (patrolPoints.length > 0) {
            currentPatrolIndex = (currentPatrolIndex + 1) % (patrolPoints.length / 2);
        }
    }
    
    /**
     * Stuns the AI for a specified duration.
     * @param duration the stun duration
     * @param currentTime the current time
     */
    public void stun(float duration, float currentTime) {
        this.stunDuration = duration;
        setState(AIState.STUNNED, currentTime);
    }
    
    /**
     * Checks if the AI should retreat based on health.
     * @param healthPercentage the current health percentage (0-1)
     * @return true if should retreat, false otherwise
     */
    public boolean shouldRetreat(float healthPercentage) {
        return healthPercentage <= retreatThreshold;
    }
    
    /**
     * Called when entering a new state.
     * @param state the state being entered
     * @param currentTime the current time
     */
    protected void onStateEnter(AIState state, float currentTime) {
        // Override in subclasses for custom behavior
    }
    
    /**
     * Called when exiting a state.
     * @param state the state being exited
     * @param currentTime the current time
     */
    protected void onStateExit(AIState state, float currentTime) {
        // Override in subclasses for custom behavior
    }
    
    @Override
    public void reset() {
        super.reset();
        currentState = AIState.IDLE;
        behavior = AIBehavior.PASSIVE;
        target = null;
        lastStateChange = 0f;
        stateTimer = 0f;
        detectionRange = 100f;
        attackRange = 50f;
        chaseRange = 200f;
        retreatThreshold = 0.2f;
        attackCooldown = 2f;
        lastAttackTime = 0f;
        patrolPoints = new float[0];
        currentPatrolIndex = 0;
        patrolSpeed = 50f;
        idleTimeout = 5f;
        searchTimeout = 10f;
        stunDuration = 3f;
    }
    
    @Override
    public String toString() {
        return String.format("AILogic{state=%s, behavior=%s, target=%s, timer=%.1fs}", 
                currentState, behavior, target != null ? target.getId() : "none", stateTimer);
    }
} 