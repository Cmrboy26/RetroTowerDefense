package net.cmr.rtd.game.world.entities.effects;

import net.cmr.rtd.game.world.Entity;
import net.cmr.rtd.game.world.entities.effects.EntityEffects.EntityStat;

/**
 * Alters the behavior of an entity.
 */
public abstract class Effect {
    
    EntityEffects target;
    float duration;
    int level;
    final float maxDuration;

    protected final float NOTHING = 1;

    public Effect(EntityEffects target, float duration, int level) {
        this(target, duration, duration, level);
    }

    public Effect(EntityEffects target, float duration, float maxDuration, int level) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if (level < 1) {
            throw new IllegalArgumentException("Level must be greater than 0");
        }
        this.target = target;
        this.duration = duration;
        this.maxDuration = maxDuration;
        this.level = level;
        this.target.addEffect(this);
    }

    public void update(float delta) {
        duration -= delta;
    }

    public boolean effectFinished() {
        return duration <= 0;
    }

    public float getDuration() {
        return duration;
    }
    public float getMaxDuration() {
        return maxDuration;
    }
    public Entity getEntity() {
        return target.getEntity();
    }
    public int getLevel() {
        return level;
    }
    public EntityEffects getTarget() {
        return target;
    }

    /**
     * Returns a multiplier for the given stat.
     * If the stat is not affected by this effect, 1 is returned.
     * @param stat the stat to modify
     * @return the multiplier for the stat
     */
    public abstract float getStatModifier(EntityStat stat);

    public String toString() {
        return getClass().getSimpleName() + "[" + duration + "s / " + maxDuration + "s, level=" + level + "]";
    }

}
