package com.ap.duelyst.model.buffs;

import com.ap.duelyst.model.buffs.traget.EffectType;
import com.ap.duelyst.model.buffs.traget.RangeType;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;
import com.ap.duelyst.model.cards.Hero;

public class PowerBuff extends Buff {
    private EffectType effectType;
    private int improve;

    public PowerBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range,
                     EffectType effectType, int improve) {
        super(duration, continuous, target, side, range);
        this.effectType = effectType;
        this.improve = improve;
    }

    public int getImprove() {
        return improve;
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public void setImprove(int improve) {
        this.improve = improve;
    }

    public void setEffectType(EffectType effectType) {
        this.effectType = effectType;
    }


    @Override
    public void applyBuff(Hero hero) {
        if (getDuration() == -1 || getRemainingTime() > 0) {
            if (getEffectType().equals(EffectType.HEALTH)) {
                hero.addHealthPointInGame(improve);
            } else if (getEffectType().equals(EffectType.ATTACK_POWER)) {
                hero.addAttackPowerInGame(improve);
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
        if (getEffectType().equals(EffectType.HEALTH)) {
            hero.addHealthPointInGame(-improve);
        } else if (getEffectType().equals(EffectType.ATTACK_POWER)) {
            hero.addAttackPowerInGame(-improve);
        }
    }
}
