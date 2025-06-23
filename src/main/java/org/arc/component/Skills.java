package org.arc.component;

import org.arc.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Skills component for entities that can use special abilities.
 * Manages skill cooldowns, mana costs, and skill progression.
 * 
 * @author Arriety
 */
public class Skills extends Component {
    
    private final ConcurrentHashMap<String, Skill> skills;
    private float globalCooldown = 0f;
    private final float globalCooldownDuration = 1.0f; // Global cooldown in seconds
    
    /**
     * Creates a skills component.
     */
    public Skills() {
        skills = new ConcurrentHashMap<>();
    }
    
    /**
     * Adds a skill to this component.
     * @param skill the skill to add
     */
    public void addSkill(Skill skill) {
        skills.put(skill.getId(), skill);
    }
    
    /**
     * Removes a skill from this component.
     * @param skillId the skill ID to remove
     * @return the removed skill, or null if not found
     */
    public Skill removeSkill(String skillId) {
        return skills.remove(skillId);
    }
    
    /**
     * Gets a skill by ID.
     * @param skillId the skill ID
     * @return the skill, or null if not found
     */
    public Skill getSkill(String skillId) {
        return skills.get(skillId);
    }
    
    /**
     * Checks if a skill can be used.
     * @param skillId the skill ID
     * @param currentTime the current time
     * @return true if the skill can be used, false otherwise
     */
    public boolean canUseSkill(String skillId, float currentTime) {
        if (globalCooldown > currentTime) {
            return false; // Global cooldown active
        }
        
        Skill skill = skills.get(skillId);
        return skill != null && skill.canUse(currentTime);
    }
    
    /**
     * Uses a skill.
     * @param skillId the skill ID
     * @param currentTime the current time
     * @return true if the skill was used, false otherwise
     */
    public boolean useSkill(String skillId, float currentTime) {
        if (!canUseSkill(skillId, currentTime)) {
            return false;
        }
        
        Skill skill = skills.get(skillId);
        if (skill.use(currentTime)) {
            globalCooldown = currentTime + globalCooldownDuration;
            return true;
        }
        
        return false;
    }
    
    /**
     * Updates all skill cooldowns.
     * @param currentTime the current time
     */
    public void updateCooldowns(float currentTime) {
        for (Skill skill : skills.values()) {
            skill.updateCooldown(currentTime);
        }
    }
    
    /**
     * Gets all skills.
     * @return a map of all skills
     */
    public Map<String, Skill> getAllSkills() {
        return new ConcurrentHashMap<>(skills);
    }
    
    /**
     * Checks if global cooldown is active.
     * @param currentTime the current time
     * @return true if global cooldown is active, false otherwise
     */
    public boolean isGlobalCooldownActive(float currentTime) {
        return globalCooldown > currentTime;
    }
    
    /**
     * Gets the remaining global cooldown time.
     * @param currentTime the current time
     * @return the remaining global cooldown time
     */
    public float getGlobalCooldownRemaining(float currentTime) {
        return Math.max(0f, globalCooldown - currentTime);
    }
    
    @Override
    public void reset() {
        super.reset();
        skills.clear();
        globalCooldown = 0f;
    }
    
    @Override
    public String toString() {
        return String.format("Skills{count=%d, globalCD=%.1fs}", skills.size(), globalCooldown);
    }
    
    /**
     * Represents a single skill with cooldown and cost mechanics.
     */
    public static class Skill {
        private final String id;
        private final String name;
        private final float cooldownDuration;
        private final int manaCost;
        private final int level;
        
        private float lastUsedTime = 0f;
        private boolean onCooldown = false;
        
        /**
         * Creates a new skill.
         * @param id the skill ID
         * @param name the skill name
         * @param cooldownDuration the cooldown duration in seconds
         * @param manaCost the mana cost
         * @param level the skill level
         */
        public Skill(String id, String name, float cooldownDuration, int manaCost, int level) {
            this.id = id;
            this.name = name;
            this.cooldownDuration = cooldownDuration;
            this.manaCost = manaCost;
            this.level = level;
        }
        
        /**
         * Gets the skill ID.
         * @return the skill ID
         */
        public String getId() {
            return id;
        }
        
        /**
         * Gets the skill name.
         * @return the skill name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Gets the cooldown duration.
         * @return the cooldown duration in seconds
         */
        public float getCooldownDuration() {
            return cooldownDuration;
        }
        
        /**
         * Gets the mana cost.
         * @return the mana cost
         */
        public int getManaCost() {
            return manaCost;
        }
        
        /**
         * Gets the skill level.
         * @return the skill level
         */
        public int getLevel() {
            return level;
        }
        
        /**
         * Checks if the skill can be used.
         * @param currentTime the current time
         * @return true if the skill can be used, false otherwise
         */
        public boolean canUse(float currentTime) {
            return !onCooldown || (currentTime - lastUsedTime) >= cooldownDuration;
        }
        
        /**
         * Uses the skill.
         * @param currentTime the current time
         * @return true if the skill was used, false otherwise
         */
        public boolean use(float currentTime) {
            if (!canUse(currentTime)) {
                return false;
            }
            
            lastUsedTime = currentTime;
            onCooldown = true;
            return true;
        }
        
        /**
         * Updates the skill cooldown.
         * @param currentTime the current time
         */
        public void updateCooldown(float currentTime) {
            if (onCooldown && (currentTime - lastUsedTime) >= cooldownDuration) {
                onCooldown = false;
            }
        }
        
        /**
         * Gets the remaining cooldown time.
         * @param currentTime the current time
         * @return the remaining cooldown time
         */
        public float getCooldownRemaining(float currentTime) {
            if (!onCooldown) {
                return 0f;
            }
            return Math.max(0f, cooldownDuration - (currentTime - lastUsedTime));
        }
        
        /**
         * Checks if the skill is on cooldown.
         * @param currentTime the current time
         * @return true if on cooldown, false otherwise
         */
        public boolean isOnCooldown(float currentTime) {
            updateCooldown(currentTime);
            return onCooldown;
        }
        
        @Override
        public String toString() {
            return String.format("Skill{id='%s', name='%s', lvl=%d, cd=%.1fs, cost=%d mana}", 
                    id, name, level, cooldownDuration, manaCost);
        }
    }
} 