package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;

import java.util.List;

public class StunBuff extends Buff {

    public StunBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range) {
        super(duration, continuous, target, side, range);
    }


    @Override
    public void applyBuff(List<Hero> heroes) {
        if (getDuration() == -1 || getRemainingTime() > 0) {
            for (Hero hero : heroes) {
                hero.setMovable(false);
            }
        }
        if (getDuration() == -1) {
            setDuration(0);
        }
        decreaseRemaningTime();
    }

    @Override
    void inactivate(List<Hero> heroes) {
        setRemainingTime(0);
        if (isContinuous()) {
            setDuration(-1);
        }
        for (Hero hero : heroes) {
            hero.setMovable(true);
        }
    }
}
