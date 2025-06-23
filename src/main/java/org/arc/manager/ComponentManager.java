package org.arc.manager;

import org.arc.BaseSystem;
import org.arc.annotations.SkipWire;

/**
 * @author Arriety
 */

@SkipWire
public class ComponentManager extends BaseSystem {

    public ComponentManager(int entityContainerSize) {

    }

    public void ensureCapacity(int newSize) {
        typeFactory.initialMapperCapacity = newSize;
        entityToIdentity.ensureCapacity(newSize);
        for (ComponentMapper mapper : mappers) {
            mapper.components.ensureCapacity(newSize);
        }
    }
}
