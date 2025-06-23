package org.arc;

import org.arc.manager.ComponentManager;
import org.arc.manager.EntityManager;
import org.arc.utils.Bag;

import static org.arc.WorldConfiguration.COMPONENT_MANAGER_IDX;
import static org.arc.WorldConfiguration.ENTITY_MANAGER_IDX;

/**
 * @author Arriety
 */
public class World {

    private final EntityManager em;

    private final ComponentManager cm;

    final Bag<BaseSystem> systemsBag;



    public World() {
        this(new WorldConfiguration());
    }

    public World(WorldConfiguration config) {

        systemsBag = config.getSystems();

        final ComponentManager lcm = (ComponentManager) systemsBag.get(COMPONENT_MANAGER_IDX);
        final EntityManager lem = (EntityManager) systemsBag.get(ENTITY_MANAGER_IDX);

        cm = lcm == null ? new ComponentManager(config.expectedEntityCount()) : lcm;
        em = lem == null ? new EntityManager(config.expectedEntityCount()) : lem;
    }

    public Entity createEntity() {
        Entity e = em.createEntityInstance();
    }

    public ComponentManager getComponentManager() {
        return cm;
    }
}
