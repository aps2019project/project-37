package duelyst.model.buffs;

import duelyst.model.buffs.traget.RangeType;
import duelyst.model.buffs.traget.SideType;
import duelyst.model.buffs.traget.TargetType;
import duelyst.model.cards.Hero;

public class KingsGuardBuff extends Buff {

    public KingsGuardBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range) {
        super(duration, continuous, target, side, range);
        setRandom(true);
    }

    @Override
    public void applyBuff(Hero hero) {
        if (hero.isImmuneToAllSpells()) {
            return;
        }
        hero.setHealthPointInGame(0);
    }

    @Override
    public void inactivate(Hero hero) {

    }
}
