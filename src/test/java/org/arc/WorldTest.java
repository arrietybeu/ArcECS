package org.arc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.arc.component.*;
import org.arc.system.MovementSystem;
import org.arc.system.HealthSystem;
import org.arc.system.AISystem;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test for the ECS framework.
 * @author Arriety
 */
public class WorldTest {
    
    private World world;
    
    @BeforeEach
    public void setUp() {
        WorldConfiguration config = new WorldConfiguration()
                .setExpectedEntityCount(100)
                .setExpectedComponentCount(500);
        
        world = new World(config);
        world.addSystem(new MovementSystem())
             .addSystem(new HealthSystem())
             .addSystem(new AISystem());
        world.initialize();
    }
    
    @AfterEach
    public void tearDown() {
        if (world != null) {
            world.dispose();
        }
    }

    @Test
    public void testEntityCreation() {
        Entity entity = world.createEntity();
        
        assertNotNull(entity);
        assertTrue(entity.getId() >= 0);
        assertFalse(entity.isDeleted());
        assertEquals(world, entity.getWorld());
    }
    
    @Test
    public void testComponentAddRemove() {
        Entity entity = world.createEntity();
        Transform transform = new Transform(10f, 20f);
        
        // Add component
        entity.addComponent(transform);
        assertTrue(entity.hasComponent(Transform.class));
        assertEquals(transform, entity.getComponent(Transform.class));
        
        // Remove component
        Transform removed = entity.removeComponent(Transform.class);
        assertFalse(entity.hasComponent(Transform.class));
        assertEquals(transform, removed);
        assertNull(entity.getComponent(Transform.class));
    }
    
    @Test
    public void testMovementSystem() {
        Entity entity = world.createEntity();
        Transform transform = new Transform(0f, 0f);
        Movement movement = new Movement(100f, 0.5f);
        movement.setVelocity(50f, 0f);
        
        entity.addComponent(transform);
        entity.addComponent(movement);
        
        // Update world
        float deltaTime = 1f/60f;
        world.update(deltaTime);
        
        // Check that position was updated
        assertTrue(transform.x > 0f);
        assertEquals(0f, transform.y, 0.001f);
    }
    
    @Test
    public void testHealthSystem() {
        Entity entity = world.createEntity();
        Health health = new Health(100f);
        health.setHealthRegenRate(10f); // 10 HP per second
        
        entity.addComponent(health);
        
        // Take damage
        health.takeDamage(50f, 0f);
        assertEquals(50f, health.getCurrentHealth(), 0.001f);
        
        // Update world to trigger regeneration
        world.update(1f); // 1 second
        
        // Health should have regenerated
        assertEquals(60f, health.getCurrentHealth(), 0.001f);
    }
    
    @Test
    public void testSkillSystem() {
        Skills skills = new Skills();
        Skills.Skill waterBreathing = new Skills.Skill("water_breathing", "Water Breathing", 3f, 20, 1);
        
        skills.addSkill(waterBreathing);
        
        // Test skill usage
        assertTrue(skills.canUseSkill("water_breathing", 0f));
        assertTrue(skills.useSkill("water_breathing", 0f));
        
        // Should be on cooldown now
        assertFalse(skills.canUseSkill("water_breathing", 1f));
        
        // Should be usable after cooldown
        assertTrue(skills.canUseSkill("water_breathing", 4f));
    }
    
    @Test
    public void testAILogic() {
        AILogic ai = new AILogic(AILogic.AIBehavior.AGGRESSIVE);
        
        assertEquals(AILogic.AIState.IDLE, ai.getCurrentState());
        assertEquals(AILogic.AIBehavior.AGGRESSIVE, ai.getBehavior());
        
        // Test state transitions
        ai.setState(AILogic.AIState.PATROL, 0f);
        assertEquals(AILogic.AIState.PATROL, ai.getCurrentState());
        
        // Test patrol points
        ai.setPatrolPoints(0f, 0f, 100f, 0f, 100f, 100f);
        float[] firstPoint = ai.getCurrentPatrolPoint();
        assertNotNull(firstPoint);
        assertEquals(0f, firstPoint[0], 0.001f);
        assertEquals(0f, firstPoint[1], 0.001f);
    }
    
    @Test
    public void testSelectButtonNPC() {
        SelectButton selectButton = new SelectButton("Test NPC");
        selectButton.setDialogText("Hello, traveler!");
        
        // Add buttons
        selectButton.addButton("shop", "Open Shop", SelectButton.ButtonAction.OPEN_SHOP);
        selectButton.addButton("quest", "View Quests", SelectButton.ButtonAction.START_QUEST);
        
        // Test button state
        assertTrue(selectButton.isButtonEnabled("shop"));
        assertEquals("Open Shop", selectButton.getButtonText("shop"));
        assertEquals(SelectButton.ButtonAction.OPEN_SHOP, selectButton.getButtonAction("shop"));
        
        // Test disabling buttons
        selectButton.setButtonEnabled("shop", false);
        assertFalse(selectButton.isButtonEnabled("shop"));
    }
    
    @Test
    public void testCompletePlayerEntity() {
        // Create a complete player entity like in a real game
        Entity player = world.createEntity();
        
        // Add all player components
        player.addComponent(new Transform(100f, 100f))
              .addComponent(new Health(100f))
              .addComponent(new Movement(200f, 0.8f));
        
        Skills skills = new Skills();
        skills.addSkill(new Skills.Skill("water_breathing", "Water Breathing: First Form", 3f, 20, 1));
        skills.addSkill(new Skills.Skill("dance_of_fire_god", "Dance of the Fire God", 8f, 40, 5));
        player.addComponent(skills);
        
        // Verify all components are present
        assertTrue(player.hasComponent(Transform.class));
        assertTrue(player.hasComponent(Health.class));
        assertTrue(player.hasComponent(Movement.class));
        assertTrue(player.hasComponent(Skills.class));
        
        // Test component interaction
        Health health = player.getComponent(Health.class);
        assertNotNull(health);
        assertEquals(100f, health.getMaxHealth(), 0.001f);
        
        Skills playerSkills = player.getComponent(Skills.class);
        assertNotNull(playerSkills);
        assertEquals(2, playerSkills.getAllSkills().size());
    }
    
    @Test
    public void testWorldStatistics() {
        // Create multiple entities
        Entity player = world.createEntity();
        Entity boss = world.createEntity();
        Entity npc = world.createEntity();
        
        player.addComponent(new Transform()).addComponent(new Health(100f));
        boss.addComponent(new Transform()).addComponent(new AILogic());
        npc.addComponent(new Transform()).addComponent(new SelectButton());
        
        // Check world statistics
        assertEquals(3, world.getEntityCount());
        assertEquals(3, world.getSystemCount());
        assertTrue(world.isInitialized());
        
        // Delete an entity
        world.deleteEntity(npc);
        assertEquals(2, world.getEntityCount());
    }

    @Test
    public void sandbox() {
        // Original sandbox test - can be used for experimentation
        System.out.println("ECS Framework is working!");
        System.out.println("World configuration: " + world.getConfiguration());
        System.out.println("Entity count: " + world.getEntityCount());
        System.out.println("System count: " + world.getSystemCount());
    }
}
