package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;
import model.game.Player;

import java.util.List;

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
