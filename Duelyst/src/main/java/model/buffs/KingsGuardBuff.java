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
    void applyBuff(List<Hero> heroes) {
        for (Hero hero : heroes) {
            if (getTarget().getType().contains(hero.getClass().getSimpleName()) || getTarget() == TargetType.CELL) {
                hero.setHealthPointInGame(0);
            }
        }

    }

    @Override
    void inactivate(List<Hero> heroes) {

    }
}
