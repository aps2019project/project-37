package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;

import java.util.List;

public class DisarmBuff extends Buff {

    public DisarmBuff(int duration, boolean continuous, TargetType target, SideType side,
                      RangeType range) {
        super(duration, continuous, target, side, range);
    }

    @Override
    public void applyBuff(Hero hero) {
        if (hero.isImmuneToAllSpells()){
            return;
        }
        if (getDuration() == -1 || getRemainingTime() > 0) {
            if (hero.isCanBeDisarmed()) {
                hero.setArmed(false);
            }
            if (getDuration() == -1) {
                setDuration(0);
            }
            decreaseRemainingTime();
        }
    }

    @Override
    public void inactivate(Hero hero) {
        setRemainingTime(0);
        if (isContinuous()) {
            setDuration(-1);
        }
        hero.setArmed(true);

    }
}
