package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;

import java.util.List;

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
