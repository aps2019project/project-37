package com.ap.duelyst.model.buffs;

import com.ap.duelyst.model.buffs.traget.RangeType;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;
import com.ap.duelyst.model.cards.Hero;

import java.util.ArrayList;
import java.util.List;

public class DispelBuff extends Buff {

    public DispelBuff(int duration, boolean continuous, TargetType target,
                      SideType side, RangeType range) {
        super(duration, continuous, target, side, range);
    }

    @Override
    public void applyBuff(Hero hero) {
        if (hero.isImmuneToAllSpells()) {
            return;
        }
        if (getRemainingTime() > 0) {
            List<Buff> buffs = new ArrayList<>(hero.getInGame().getBuffs());
            for (Buff buff : buffs) {
                if ((hero.getInGame().isEnemy() && buff.getSide() == SideType.ALLY)
                        || (!hero.getInGame().isEnemy() && buff.getSide() == SideType.ENEMY)) {
                    if (buff.isCancelable()) {
                        buff.inactivate(hero);
                    }
                    if (!buff.isContinuous()) {
                        hero.getInGame().removeBuff(buff);
                    }
                }
            }

            decreaseRemainingTime();
        }

    }

    @Override
    public void inactivate(Hero hero) {

    }
}
