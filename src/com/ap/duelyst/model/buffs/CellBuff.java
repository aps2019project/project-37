package com.ap.duelyst.model.buffs;

import com.ap.duelyst.model.buffs.traget.RangeType;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;
import com.ap.duelyst.model.cards.Hero;

public class CellBuff extends Buff {

    private Buff buff;

    public CellBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range, Buff buff) {
        super(duration, continuous, target, side, range);
        this.buff = buff;
    }

    @Override
    public void applyBuff(Hero hero) {
        if (getRemainingTime() > 0) {
            hero.getInGame().addBuff(buff);

            decreaseRemainingTime();
        }
    }

    @Override
    public void inactivate(Hero hero) {
        setRemainingTime(0);
    }

    public Buff getBuff() {
        return buff;
    }
}
