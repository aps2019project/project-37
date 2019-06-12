package duelyst.model.buffs;

import duelyst.model.buffs.traget.EffectType;
import duelyst.model.buffs.traget.RangeType;
import duelyst.model.buffs.traget.SideType;
import duelyst.model.buffs.traget.TargetType;

public class PoisonBuff extends WeaknessBuff {

    public PoisonBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range) {
        super(duration, continuous, target, side, range, EffectType.HEALTH, 1);
        isPoison =true;
    }

}
