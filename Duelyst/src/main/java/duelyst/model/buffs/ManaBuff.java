package duelyst.model.buffs;

import duelyst.model.buffs.traget.RangeType;
import duelyst.model.buffs.traget.SideType;
import duelyst.model.buffs.traget.TargetType;
import duelyst.model.cards.Hero;

public class ManaBuff extends Buff {

    private int amount;

    public ManaBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range, int amount) {
        super(duration, continuous, target, side, range);
        this.amount = amount;
        setCancelable(false);
    }

    @Override
    public void applyBuff(Hero hero) {

    }

    @Override
    public void inactivate(Hero hero) {

    }

    public int getAmount() {
        return amount;
    }
}
