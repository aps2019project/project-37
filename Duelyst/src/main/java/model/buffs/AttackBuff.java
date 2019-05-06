package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;

import java.util.List;

public class AttackBuff extends Buff {

    private int amount;

    public AttackBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range, int amount) {
        super(duration, continuous, target, side, range);
        this.amount = amount;
    }

    @Override
    void applyBuff(List<Hero> heroes) {
        if (getDuration() == -1 || getRemainingTime() > 0) {
            for (Hero hero : heroes) {
                hero.decreaseHealthPointInGame(amount);
            }
            if (getDuration() == -1) {
                setDuration(0);
            }
            decreaseRemaningTime();
        }
    }

    @Override
    void inactivate(List<Hero> heroes) {
        setRemainingTime(0);
        if (isContinuous()) {
            setDuration(-1);
        }
    }
}
