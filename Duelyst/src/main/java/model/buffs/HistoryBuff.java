package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryBuff extends Buff {

    private List<Integer> damages;

    public HistoryBuff(boolean continuous, TargetType target, SideType side, RangeType range,
                       Integer... damages) {
        super(0, continuous, target, side, range);
        this.damages = new ArrayList<>(Arrays.asList(damages));
        setDuration(this.damages.size());
    }

    @Override
    public void applyBuff(Hero hero) {
        if (hero.isImmuneToAllSpells()){
            return;
        }
        if (getDuration() == -1 || getRemainingTime() > 0) {
            hero.addHealthPointInGame(-damages.get(getRemainingTime() - 1));
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
