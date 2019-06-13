package com.ap.duelyst.model.buffs;

import com.ap.duelyst.model.buffs.traget.EffectType;
import com.ap.duelyst.model.buffs.traget.RangeType;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;

public class PoisonBuff extends WeaknessBuff {

    public PoisonBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range) {
        super(duration, continuous, target, side, range, EffectType.HEALTH, 1);
        isPoison =true;
    }

}
