package org.arc.system;

import org.arc.IteratingSystem;
import org.arc.Entity;
import org.arc.component.AILogic;
import org.arc.component.Health;
import org.arc.component.Movement;
import org.arc.component.Transform;
import org.arc.utils.Bag;

/**
 * AISystem processes entities with AILogic components.
 * Manages AI state transitions and behaviors for monsters and bosses.
 * 
 * @author Arriety
 */
public class AISystem extends IteratingSystem {
    
    public AISystem() {
        require(AILogic.class);
        require(Transform.class);
    }
    
    @Override
    protected void process(Entity entity, float deltaTime) {
        float currentTime = world.getDelta(); // This should be accumulated time
        
        AILogic ai = getComponent(entity, AILogic.class);
        if (ai != null) {
            updateAI(entity, ai, currentTime, deltaTime);
        }
    }
    
    /**
     * Updates AI for a single entity.
     * @param entity the entity
     * @param ai the AI logic component
     * @param currentTime the current time
     * @param deltaTime the time delta
     */
    private void updateAI(Entity entity, AILogic ai, float currentTime, float deltaTime) {
        ai.updateStateTimer(deltaTime);
        
        // Check health for retreat logic
        Health health = entity.getComponent(Health.class);
        if (health != null && ai.shouldRetreat(health.getHealthPercentage())) {
            if (ai.getCurrentState() != AILogic.AIState.RETREAT && 
                ai.getCurrentState() != AILogic.AIState.DEAD) {
                ai.setState(AILogic.AIState.RETREAT, currentTime);
            }
        }
        
        // Handle state-specific logic
        switch (ai.getCurrentState()) {
            case IDLE:
                handleIdleState(entity, ai, currentTime);
                break;
            case PATROL:
                handlePatrolState(entity, ai, currentTime, deltaTime);
                break;
            case CHASE:
                handleChaseState(entity, ai, currentTime, deltaTime);
                break;
            case ATTACK:
                handleAttackState(entity, ai, currentTime, deltaTime);
                break;
            case RETREAT:
                handleRetreatState(entity, ai, currentTime, deltaTime);
                break;
            case SEARCH:
                handleSearchState(entity, ai, currentTime);
                break;
            case STUNNED:
                handleStunnedState(entity, ai, currentTime);
                break;
            case DEAD:
                handleDeadState(entity, ai, currentTime);
                break;
        }
        
        // Look for targets if in appropriate behavior mode
        if (ai.getBehavior() == AILogic.AIBehavior.AGGRESSIVE || 
            ai.getBehavior() == AILogic.AIBehavior.TERRITORIAL) {
            lookForTargets(entity, ai, currentTime);
        }
    }
    
    /**
     * Handles the IDLE state.
     */
    private void handleIdleState(Entity entity, AILogic ai, float currentTime) {
        // Stay idle for a while, then potentially start patrolling
        if (ai.getStateTimer() > 5f) {
            // Could transition to patrol if patrol points are set
            float[] patrolPoint = ai.getCurrentPatrolPoint();
            if (patrolPoint != null) {
                ai.setState(AILogic.AIState.PATROL, currentTime);
            }
        }
    }
    
    /**
     * Handles the PATROL state.
     */
    private void handlePatrolState(Entity entity, AILogic ai, float currentTime, float deltaTime) {
        Transform transform = entity.getComponent(Transform.class);
        Movement movement = entity.getComponent(Movement.class);
        
        if (transform == null) return;
        
        float[] targetPoint = ai.getCurrentPatrolPoint();
        if (targetPoint == null) {
            ai.setState(AILogic.AIState.IDLE, currentTime);
            return;
        }
        
        // Move towards patrol point
        float dx = targetPoint[0] - transform.x;
        float dy = targetPoint[1] - transform.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance < 10f) {
            // Reached patrol point, move to next
            ai.nextPatrolPoint();
        } else if (movement != null) {
            // Move towards patrol point
            float speed = 50f; // Patrol speed
            movement.velocityX = (dx / distance) * speed;
            movement.velocityY = (dy / distance) * speed;
        }
    }
    
    /**
     * Handles the CHASE state.
     */
    private void handleChaseState(Entity entity, AILogic ai, float currentTime, float deltaTime) {
        Entity target = ai.getTarget();
        if (target == null || target.isDeleted()) {
            ai.setTarget(null);
            ai.setState(AILogic.AIState.SEARCH, currentTime);
            return;
        }
        
        Transform transform = entity.getComponent(Transform.class);
        Transform targetTransform = target.getComponent(Transform.class);
        
        if (transform == null || targetTransform == null) {
            ai.setState(AILogic.AIState.IDLE, currentTime);
            return;
        }
        
        float distance = transform.distanceTo(targetTransform);
        
        // Check if target is in attack range
        if (distance <= ai.getAttackRange()) {
            ai.setState(AILogic.AIState.ATTACK, currentTime);
            return;
        }
        
        // Check if target is out of chase range
        if (distance > ai.getChaseRange()) {
            ai.setTarget(null);
            ai.setState(AILogic.AIState.SEARCH, currentTime);
            return;
        }
        
        // Move towards target
        Movement movement = entity.getComponent(Movement.class);
        if (movement != null) {
            float dx = targetTransform.x - transform.x;
            float dy = targetTransform.y - transform.y;
            float speed = movement.getMaxSpeed();
            
            movement.velocityX = (dx / distance) * speed;
            movement.velocityY = (dy / distance) * speed;
        }
    }
    
    /**
     * Handles the ATTACK state.
     */
    private void handleAttackState(Entity entity, AILogic ai, float currentTime, float deltaTime) {
        Entity target = ai.getTarget();
        if (target == null || target.isDeleted()) {
            ai.setTarget(null);
            ai.setState(AILogic.AIState.IDLE, currentTime);
            return;
        }
        
        Transform transform = entity.getComponent(Transform.class);
        Transform targetTransform = target.getComponent(Transform.class);
        
        if (transform == null || targetTransform == null) {
            ai.setState(AILogic.AIState.IDLE, currentTime);
            return;
        }
        
        float distance = transform.distanceTo(targetTransform);
        
        // Check if target moved out of attack range
        if (distance > ai.getAttackRange()) {
            ai.setState(AILogic.AIState.CHASE, currentTime);
            return;
        }
        
        // Perform attack if cooldown is ready
        if (ai.canAttack(currentTime)) {
            performAttack(entity, target, ai, currentTime);
            ai.recordAttack(currentTime);
        }
    }
    
    /**
     * Handles the RETREAT state.
     */
    private void handleRetreatState(Entity entity, AILogic ai, float currentTime, float deltaTime) {
        // Simple retreat logic: move away from target
        Entity target = ai.getTarget();
        if (target != null) {
            Transform transform = entity.getComponent(Transform.class);
            Transform targetTransform = target.getComponent(Transform.class);
            Movement movement = entity.getComponent(Movement.class);
            
            if (transform != null && targetTransform != null && movement != null) {
                float dx = transform.x - targetTransform.x;
                float dy = transform.y - targetTransform.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                
                if (distance > 0) {
                    float speed = movement.getMaxSpeed();
                    movement.velocityX = (dx / distance) * speed;
                    movement.velocityY = (dy / distance) * speed;
                }
            }
        }
        
        // Check if we should stop retreating
        Health health = entity.getComponent(Health.class);
        if (health != null && health.getHealthPercentage() > 0.5f) {
            ai.setState(AILogic.AIState.IDLE, currentTime);
        }
    }
    
    /**
     * Handles the SEARCH state.
     */
    private void handleSearchState(Entity entity, AILogic ai, float currentTime) {
        // Look for targets in search mode
        lookForTargets(entity, ai, currentTime);
        
        // Timeout after searching for too long
        if (ai.getStateTimer() > 10f) {
            ai.setState(AILogic.AIState.IDLE, currentTime);
        }
    }
    
    /**
     * Handles the STUNNED state.
     */
    private void handleStunnedState(Entity entity, AILogic ai, float currentTime) {
        // Stay stunned for the duration
        if (ai.getStateTimer() > 3f) { // Default stun duration
            ai.setState(AILogic.AIState.IDLE, currentTime);
        }
    }
    
    /**
     * Handles the DEAD state.
     */
    private void handleDeadState(Entity entity, AILogic ai, float currentTime) {
        // Entity is dead, stop all AI processing
        Movement movement = entity.getComponent(Movement.class);
        if (movement != null) {
            movement.stop();
        }
    }
    
    /**
     * Looks for targets within detection range.
     */
    private void lookForTargets(Entity entity, AILogic ai, float currentTime) {
        Transform transform = entity.getComponent(Transform.class);
        if (transform == null) return;
        
        Bag<Entity> entities = world.getAllEntities();
        Entity closestTarget = null;
        float closestDistance = Float.MAX_VALUE;
        
        for (Entity potentialTarget : entities) {
            if (potentialTarget == entity || potentialTarget.isDeleted()) {
                continue;
            }
            
            // Check if this is a valid target (e.g., player, different faction)
            if (isValidTarget(entity, potentialTarget)) {
                Transform targetTransform = potentialTarget.getComponent(Transform.class);
                if (targetTransform != null) {
                    float distance = transform.distanceTo(targetTransform);
                    
                    if (distance <= ai.getDetectionRange() && distance < closestDistance) {
                        closestTarget = potentialTarget;
                        closestDistance = distance;
                    }
                }
            }
        }
        
        if (closestTarget != null) {
            ai.setTarget(closestTarget);
            ai.setState(AILogic.AIState.CHASE, currentTime);
        }
    }
    
    /**
     * Checks if an entity is a valid target for the AI.
     * @param aiEntity the AI entity
     * @param potentialTarget the potential target
     * @return true if valid target, false otherwise
     */
    private boolean isValidTarget(Entity aiEntity, Entity potentialTarget) {
        // This is a placeholder - in a real game you'd check factions,
        // player status, etc.
        Health targetHealth = potentialTarget.getComponent(Health.class);
        return targetHealth != null && !targetHealth.isDead();
    }
    
    /**
     * Performs an attack on the target.
     * @param attacker the attacking entity
     * @param target the target entity
     * @param ai the AI logic component
     * @param currentTime the current time
     */
    private void performAttack(Entity attacker, Entity target, AILogic ai, float currentTime) {
        Health targetHealth = target.getComponent(Health.class);
        if (targetHealth != null) {
            // Simple damage calculation - in a real game this would be more complex
            float damage = 10f; // Base damage
            targetHealth.takeDamage(damage, currentTime);
        }
    }
} 