package duelyst.model.buffs;

import duelyst.model.buffs.traget.RangeType;
import duelyst.model.buffs.traget.SideType;
import duelyst.model.buffs.traget.TargetType;
import duelyst.model.cards.Hero;

public class AttackBuff extends Buff {

    private int amount;

    public AttackBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range, int amount) {
        super(duration, continuous, target, side, range);
        this.amount = amount;
    }

    @Override
    public void applyBuff(Hero hero) {
        if (hero.isImmuneToAllSpells()){
            return;
        }
        if (getDuration() == -1 || getRemainingTime() > 0) {
            hero.decreaseHealthPointInGame(amount);
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
    }
}
