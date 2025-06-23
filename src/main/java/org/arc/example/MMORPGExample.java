package org.arc.example;

import org.arc.Entity;
import org.arc.World;
import org.arc.WorldConfiguration;
import org.arc.component.*;
import org.arc.system.AISystem;
import org.arc.system.HealthSystem;
import org.arc.system.MovementSystem;

/**
 * Example demonstrating the ECS framework for a Demon Slayer inspired MMO RPG.
 * Shows how to create different entity types (Player, Boss, Monster, NPC) with
 * various component combinations.
 * 
 * @author Arriety
 */
public class MMORPGExample {
    
    public static void main(String[] args) {
        // Create a world with custom configuration
        WorldConfiguration config = new WorldConfiguration()
                .setExpectedEntityCount(1000)
                .setExpectedComponentCount(5000)
                .setEntityCaching(true)
                .setFixedTimeStep(1f/60f); // 60 FPS
        
        World world = new World(config);
        
        // Add systems to the world
        world.addSystem(new MovementSystem())
             .addSystem(new HealthSystem())
             .addSystem(new AISystem());
        
        // Initialize the world
        world.initialize();
        
        // Create example entities
        Entity player = createPlayer(world, "Tanjiro", 100, 300);
        Entity boss = createBoss(world, "Muzan", 1000, 150, 200);
        Entity monster = createDemon(world, "Lesser Demon", 50, 80, 50, 100);
        Entity npc = createShopNPC(world, "Urokodaki", 200, 50);
        
        System.out.println("=== Demon Slayer MMO RPG ECS Demo ===");
        System.out.println("World: " + world);
        System.out.println();
        
        // Print entity information
        printEntityInfo("Player", player);
        printEntityInfo("Boss", boss);
        printEntityInfo("Monster", monster);
        printEntityInfo("NPC", npc);
        
        // Simulate game loop
        System.out.println("\n=== Simulating Game Updates ===");
        float deltaTime = 1f/60f; // 60 FPS
        
        for (int frame = 0; frame < 300; frame++) { // 5 seconds at 60 FPS
            world.update(deltaTime);
            
            // Print updates every second
            if (frame % 60 == 0) {
                int second = frame / 60;
                System.out.printf("--- Second %d ---\n", second);
                
                Transform playerTransform = player.getComponent(Transform.class);
                Health playerHealth = player.getComponent(Health.class);
                System.out.printf("Player: Pos(%.1f, %.1f) Health(%.1f/%.1f)\n", 
                        playerTransform.x, playerTransform.y, 
                        playerHealth.getCurrentHealth(), playerHealth.getMaxHealth());
                
                AILogic bossAI = boss.getComponent(AILogic.class);
                Health bossHealth = boss.getComponent(Health.class);
                System.out.printf("Boss: State(%s) Health(%.1f/%.1f)\n", 
                        bossAI.getCurrentState(), 
                        bossHealth.getCurrentHealth(), bossHealth.getMaxHealth());
                
                // Test skill usage on player
                Skills playerSkills = player.getComponent(Skills.class);
                if (playerSkills.canUseSkill("water_breathing", frame * deltaTime)) {
                    playerSkills.useSkill("water_breathing", frame * deltaTime);
                    System.out.println("Player used Water Breathing technique!");
                }
            }
        }
        
        // Cleanup
        world.dispose();
        System.out.println("\nWorld disposed. Demo complete.");
    }
    
    /**
     * Creates a player entity with combat and movement capabilities.
     */
    private static Entity createPlayer(World world, String name, float health, float speed) {
        Entity player = world.createEntity();
        
        // Core components
        player.addComponent(new Transform(50, 50));
        player.addComponent(new Health(health));
        player.addComponent(new Movement(speed, 0.9f));
        
        // Player-specific components
        Skills skills = new Skills();
        skills.addSkill(new Skills.Skill("water_breathing", "Water Breathing: First Form", 3f, 20, 1));
        skills.addSkill(new Skills.Skill("dance_of_fire_god", "Dance of the Fire God", 8f, 40, 5));
        player.addComponent(skills);
        
        return player;
    }
    
    /**
     * Creates a boss entity with AI, skills, and high health.
     */
    private static Entity createBoss(World world, String name, float health, float x, float y) {
        Entity boss = world.createEntity();
        
        // Core components
        boss.addComponent(new Transform(x, y));
        boss.addComponent(new Health(health));
        boss.addComponent(new Movement(120f, 0.7f));
        
        // AI configuration for boss behavior
        AILogic ai = new AILogic(AILogic.AIBehavior.BOSS);
        ai.setDetectionRange(200f);
        ai.setAttackRange(80f);
        ai.setChaseRange(300f);
        boss.addComponent(ai);
        
        // Boss skills
        Skills skills = new Skills();
        skills.addSkill(new Skills.Skill("demon_art", "Blood Demon Art", 5f, 0, 10));
        skills.addSkill(new Skills.Skill("regeneration", "Rapid Regeneration", 15f, 0, 8));
        boss.addComponent(skills);
        
        return boss;
    }
    
    /**
     * Creates a demon monster with basic AI.
     */
    private static Entity createDemon(World world, String name, float health, float speed, float x, float y) {
        Entity demon = world.createEntity();
        
        // Core components
        demon.addComponent(new Transform(x, y));
        demon.addComponent(new Health(health));
        demon.addComponent(new Movement(speed, 0.8f));
        
        // Basic AI for monsters
        AILogic ai = new AILogic(AILogic.AIBehavior.AGGRESSIVE);
        ai.setDetectionRange(100f);
        ai.setAttackRange(30f);
        ai.setChaseRange(150f);
        // Set patrol points
        ai.setPatrolPoints(x - 50, y, x + 50, y, x, y - 30, x, y + 30);
        demon.addComponent(ai);
        
        return demon;
    }
    
    /**
     * Creates an NPC with interactive buttons.
     */
    private static Entity createShopNPC(World world, String name, float x, float y) {
        Entity npc = world.createEntity();
        
        // Core components
        npc.addComponent(new Transform(x, y));
        npc.addComponent(new Health(100f)); // NPCs can have health too
        
        // NPC interaction system
        SelectButton selectButton = new SelectButton(name);
        selectButton.setDialogText("Welcome, young demon slayer! What can I do for you?");
        selectButton.addButton("shop", "Open Shop", SelectButton.ButtonAction.OPEN_SHOP);
        selectButton.addButton("upgrade", "Upgrade Equipment", SelectButton.ButtonAction.UPGRADE_EQUIPMENT);
        selectButton.addButton("quest", "View Quests", SelectButton.ButtonAction.START_QUEST);
        selectButton.addButton("gift", "Enter Gift Code", SelectButton.ButtonAction.ENTER_GIFT_CODE);
        npc.addComponent(selectButton);
        
        return npc;
    }
    
    /**
     * Prints detailed information about an entity.
     */
    private static void printEntityInfo(String type, Entity entity) {
        System.out.printf("=== %s (ID: %d) ===\n", type, entity.getId());
        
        Transform transform = entity.getComponent(Transform.class);
        if (transform != null) {
            System.out.println("  " + transform);
        }
        
        Health health = entity.getComponent(Health.class);
        if (health != null) {
            System.out.println("  " + health);
        }
        
        Movement movement = entity.getComponent(Movement.class);
        if (movement != null) {
            System.out.println("  " + movement);
        }
        
        Skills skills = entity.getComponent(Skills.class);
        if (skills != null) {
            System.out.println("  " + skills);
            skills.getAllSkills().values().forEach(skill -> 
                System.out.println("    - " + skill));
        }
        
        AILogic ai = entity.getComponent(AILogic.class);
        if (ai != null) {
            System.out.println("  " + ai);
        }
        
        SelectButton selectButton = entity.getComponent(SelectButton.class);
        if (selectButton != null) {
            System.out.println("  " + selectButton);
            selectButton.getAllButtons().forEach((id, text) -> 
                System.out.println("    - " + id + ": " + text));
        }
        
        System.out.println();
    }
} 