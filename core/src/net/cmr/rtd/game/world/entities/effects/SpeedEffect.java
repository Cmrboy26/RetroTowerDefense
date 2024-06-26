package net.cmr.rtd.game.world.entities.effects;

import net.cmr.rtd.game.world.UpdateData;
import net.cmr.rtd.game.world.entities.effects.EntityEffects.EntityStat;

public class SpeedEffect extends Effect {

    public SpeedEffect(UpdateData data, EntityEffects target, float duration, float maxDuration, int level) {
        super(data, target, duration, maxDuration, level);
    }

    public SpeedEffect(UpdateData data, EntityEffects target, float duration, int level) {
        super(data, target, duration, level);
    }

    @Override
    public float getStatModifier(EntityStat stat) {
        if (stat == EntityStat.SPEED) {
            double result = 1 + 2d/3d * Math.log(getLevel() + 1); 
            return (float) result;
        }
        return NOTHING;    
    }
    
}
