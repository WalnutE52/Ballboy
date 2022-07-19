package ballboy.model.entities;

import ballboy.model.Entity;
import ballboy.model.Level;

/**
 * A static entity instance depended on by Levels.
 */
public abstract class StaticEntity implements Entity {

    public abstract Entity copy();
}
