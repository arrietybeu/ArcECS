 # ECS Core - Entity Component System Framework

A high-performance, modular Entity-Component-System (ECS) framework designed for 2D side-scrolling real-time MMO RPGs, inspired by Kimetsu no Yaiba (Demon Slayer).

## Features

- 🎮 **Game-Ready Components**: Transform, Health, Movement, Skills, AI Logic, and Interactive NPCs
- 🚀 **High Performance**: Efficient bit-vector based component matching and entity management
- 🔧 **Runtime Flexibility**: Add/remove components dynamically during gameplay
- 🎯 **Type-Safe**: Fully typed component system with compile-time safety
- 🧠 **Advanced AI**: State-machine based AI system with patrol, chase, attack, and retreat behaviors
- 🎨 **Modular Design**: Clean separation of concerns following ECS principles
- 💾 **Memory Efficient**: Component pooling and entity recycling support
- 📊 **Observable**: Built-in statistics and profiling capabilities

## Architecture

### Core Classes

- **`World`**: Main container orchestrating entities, components, and systems
- **`Entity`**: Lightweight containers identified by unique IDs
- **`Component`**: Pure data containers defining entity properties
- **`BaseSystem`**: Logic processors that operate on entities with specific component combinations

### Key Components

| Component | Purpose | Use Cases |
|-----------|---------|-----------|
| `Transform` | Position, rotation, scale | All visible entities |
| `Health` | HP, damage, regeneration | Players, monsters, bosses |
| `Movement` | Velocity, acceleration, physics | Moving entities |
| `Skills` | Abilities, cooldowns, mana costs | Players, bosses |
| `AILogic` | AI states, behaviors, targeting | Monsters, bosses |
| `SelectButton` | Interactive UI buttons | NPCs, shops, quest givers |

### Built-in Systems

- **`MovementSystem`**: Handles position updates, physics, and collision
- **`HealthSystem`**: Manages health regeneration and death states
- **`AISystem`**: Processes AI state machines and behaviors

## Quick Start

### 1. Create a World

```java
WorldConfiguration config = new WorldConfiguration()
    .setExpectedEntityCount(1000)
    .setExpectedComponentCount(5000)
    .setEntityCaching(true);

World world = new World(config);
world.addSystem(new MovementSystem())
     .addSystem(new HealthSystem())
     .addSystem(new AISystem());
world.initialize();
```

### 2. Create Entities

#### Player Entity
```java
Entity player = world.createEntity()
    .addComponent(new Transform(50, 50))
    .addComponent(new Health(100f))
    .addComponent(new Movement(200f, 0.9f));

Skills skills = new Skills();
skills.addSkill(new Skills.Skill("water_breathing", "Water Breathing: First Form", 3f, 20, 1));
skills.addSkill(new Skills.Skill("dance_of_fire_god", "Dance of the Fire God", 8f, 40, 5));
player.addComponent(skills);
```

#### Boss Entity
```java
Entity boss = world.createEntity()
    .addComponent(new Transform(200, 200))
    .addComponent(new Health(1000f))
    .addComponent(new Movement(120f, 0.7f));

AILogic ai = new AILogic(AILogic.AIBehavior.BOSS);
ai.setDetectionRange(200f);
ai.setAttackRange(80f);
ai.setChaseRange(300f);
boss.addComponent(ai);
```

#### NPC Entity
```java
Entity npc = world.createEntity()
    .addComponent(new Transform(100, 50));

SelectButton selectButton = new SelectButton("Urokodaki");
selectButton.setDialogText("Welcome, young demon slayer!");
selectButton.addButton("shop", "Open Shop", SelectButton.ButtonAction.OPEN_SHOP);
selectButton.addButton("quest", "View Quests", SelectButton.ButtonAction.START_QUEST);
npc.addComponent(selectButton);
```

### 3. Game Loop

```java
float deltaTime = 1f/60f; // 60 FPS
while (gameRunning) {
    world.update(deltaTime);
    
    // Handle input, rendering, networking, etc.
}

world.dispose(); // Cleanup when done
```

## Advanced Usage

### Custom Components

```java
public class Inventory extends Component {
    private final Map<String, Integer> items = new HashMap<>();
    private int maxSlots = 30;
    
    public void addItem(String itemId, int quantity) {
        items.merge(itemId, quantity, Integer::sum);
    }
    
    // ... other inventory logic
}
```

### Custom Systems

```java
public class InventorySystem extends BaseSystem {
    public InventorySystem() {
        require(Inventory.class);
    }
    
    @Override
    protected void onUpdate(float deltaTime) {
        for (Entity entity : world.getAllEntities()) {
            if (matches(entity)) {
                Inventory inventory = entity.getComponent(Inventory.class);
                // Process inventory logic
            }
        }
    }
}
```

### Component Queries

```java
// Get all entities with specific components
for (Entity entity : world.getAllEntities()) {
    if (entity.hasComponent(Health.class) && entity.hasComponent(Transform.class)) {
        Health health = entity.getComponent(Health.class);
        Transform transform = entity.getComponent(Transform.class);
        // Process entity
    }
}
```

## AI System

The AI system supports complex behaviors through state machines:

### AI States
- `IDLE`: Resting state
- `PATROL`: Following predefined paths
- `CHASE`: Pursuing targets
- `ATTACK`: Engaging in combat
- `RETREAT`: Escaping when low health
- `SEARCH`: Looking for lost targets
- `STUNNED`: Temporarily disabled
- `DEAD`: No longer active

### AI Behaviors
- `PASSIVE`: Never attacks first
- `DEFENSIVE`: Attacks when attacked
- `AGGRESSIVE`: Attacks nearby enemies
- `TERRITORIAL`: Defends specific areas
- `BOSS`: Complex multi-phase behavior

### Example AI Configuration

```java
AILogic monsterAI = new AILogic(AILogic.AIBehavior.AGGRESSIVE);
monsterAI.setDetectionRange(100f);
monsterAI.setAttackRange(30f);
monsterAI.setChaseRange(150f);
monsterAI.setPatrolPoints(0f, 0f, 100f, 0f, 100f, 100f, 0f, 100f);
```

## Skills System

The skills system provides cooldown management and skill progression:

```java
Skills playerSkills = new Skills();

// Add skills with cooldown, mana cost, and level
playerSkills.addSkill(new Skills.Skill("fireball", "Fireball", 2f, 15, 1));
playerSkills.addSkill(new Skills.Skill("heal", "Healing Light", 5f, 25, 3));

// Use skills
if (playerSkills.canUseSkill("fireball", currentTime)) {
    if (playerSkills.useSkill("fireball", currentTime)) {
        // Skill was successfully used
        // Trigger fireball effects
    }
}
```

## NPC Interaction System

Create interactive NPCs with dynamic button systems:

```java
SelectButton npcButtons = new SelectButton("Merchant");
npcButtons.setDialogText("What would you like to buy today?");

// Add context-sensitive buttons
npcButtons.addButton("buy", "Buy Items", SelectButton.ButtonAction.OPEN_SHOP);
npcButtons.addButton("sell", "Sell Items", SelectButton.ButtonAction.TRADE);

// Conditionally enable/disable buttons
if (player.hasQuestItems()) {
    npcButtons.addButton("quest", "Complete Quest", SelectButton.ButtonAction.COMPLETE_QUEST);
}
```

## Performance Tips

1. **Entity Recycling**: Use `world.deleteEntity()` to recycle entity IDs
2. **Component Pooling**: Enable in WorldConfiguration for better memory usage
3. **System Ordering**: Add systems in order of dependency (Movement before AI)
4. **Batch Operations**: Process similar entities together in systems
5. **Efficient Queries**: Use component requirements in systems to filter entities

## Testing

Run the comprehensive test suite:

```bash
gradle test
```

The framework includes tests for:
- Entity lifecycle management
- Component addition/removal
- System processing
- AI state transitions
- Skills and cooldowns
- NPC interactions

## Example: Complete MMO RPG Demo

See `MMORPGExample.java` for a complete demonstration including:
- Player with combat skills (Water Breathing, Dance of Fire God)
- Boss with complex AI and abilities
- Demons with patrol and aggression
- NPCs with shops and quest interactions
- Real-time simulation with 60 FPS updates

## License

This project is part of a Demon Slayer inspired MMO RPG server implementation.

## Contributing

1. Follow the existing code style
2. Add comprehensive tests for new features
3. Update documentation for API changes
4. Ensure backward compatibility when possible

---

*Built with ❤️ for the Demon Slayer community*