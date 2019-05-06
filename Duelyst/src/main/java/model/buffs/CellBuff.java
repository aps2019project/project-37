package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;

import java.util.List;

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
