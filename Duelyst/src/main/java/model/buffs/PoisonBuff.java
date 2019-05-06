package model.buffs;

import model.EffectType;

public class PoisonBuff extends WeaknessBuff {
    PoisonBuff(int duration , boolean continuous){
        super(duration, continuous, EffectType.HEALTH, 1);
    }

    @Override
    public PoisonBuff clone() throws CloneNotSupportedException {
        return (PoisonBuff) super.clone();
    }
}
