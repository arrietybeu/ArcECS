package org.arc;

import org.arc.annotations.SkipWire;

/**
 * @author Arriety
 */
public abstract class BaseSystem {

    @SkipWire
    protected World world;

    public BaseSystem() {}
}
