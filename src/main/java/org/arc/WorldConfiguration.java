package org.arc;

import lombok.Getter;
import org.arc.utils.Bag;
import org.arc.utils.reflect.ClassReflection;
import org.arc.utils.reflect.ReflectionException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Arriety
 */
public class WorldConfiguration {

    public static final int COMPONENT_MANAGER_IDX = 0;
    public static final int ENTITY_MANAGER_IDX = 1;

    protected int expectedEntityCount = 128;

    @Getter
    private final Bag<BaseSystem> systems = new Bag<>(BaseSystem.class);
    private Set<Class<? extends BaseSystem>> registered = new HashSet<>();

    public WorldConfiguration() {
        // reserving space for core managers
        systems.add(null); // ComponentManager
        systems.add(null); // EntityManager
        systems.add(null); // AspectSubscriptionManager
    }

    public WorldConfiguration setSystem(Class<? extends BaseSystem> system) {
        try {
            return setSystem(ClassReflection.newInstance(system));
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends BaseSystem> WorldConfiguration setSystem(T system) {
        systems.add(system);

        if (!registered.add(system.getClass())) {
            String name = system.getClass().getSimpleName();
            throw new RuntimeException(name + " already added to " + getClass().getSimpleName());
        }

        return this;
    }

    public int expectedEntityCount() {
        return expectedEntityCount;
    }


}
