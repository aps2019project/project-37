package model.buffs;

import model.buffs.traget.EffectType;
import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;

public class PoisonBuff extends WeaknessBuff {

    public PoisonBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range) {
        super(duration, continuous, target, side, range, EffectType.HEALTH, 1);
        isPoison =true;
    }

}
