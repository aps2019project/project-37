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
    void applyBuff(List<Hero> heroes) {
        if (getRemainingTime() > 0) {
            for (Hero hero : heroes) {
                hero.getInGame().addBuff(buff);
            }
            decreaseRemaningTime();
        }
    }

    @Override
    void inactivate(List<Hero> heroes) {
        setRemainingTime(0);
    }
}
