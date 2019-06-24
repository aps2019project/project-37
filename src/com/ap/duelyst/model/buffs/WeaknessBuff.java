package com.ap.duelyst.model.buffs;

import com.ap.duelyst.model.buffs.traget.EffectType;
import com.ap.duelyst.model.buffs.traget.RangeType;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;
import com.ap.duelyst.model.cards.Hero;

public class WeaknessBuff extends Buff {
    private EffectType effectType1;
    private int damage;
    boolean isPoison;

    public WeaknessBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range,
                        EffectType effectType1, int damage) {
        super(duration, continuous, target, side, range);
        this.effectType1 = effectType1;
        this.damage = damage;
    }

    public EffectType getDamageType() {
        return effectType1;
    }

    public int getDamage() {
        return damage;
    }

    public void setEffectType1(EffectType effectType1) {
        this.effectType1 = effectType1;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }


    @Override
    public void applyBuff(Hero hero) {
        if (hero.isImmuneToAllSpells()) {
            return;
        }
        if (getDuration() == -1 || getRemainingTime() > 0) {
            if (isPoison && !hero.isCanBePoisoned()) {
                return;
            }
            if (getDamageType().equals(EffectType.HEALTH)) {
                hero.addHealthPointInGame(-damage);
            } else if (getDamageType().equals(EffectType.ATTACK_POWER)) {
                hero.addAttackPowerInGame(-damage);
            }
            if (getDuration() == -1) {
                setDuration(0);
            }
            decreaseRemainingTime();
        }
    }

    @Override
    public void inactivate(Hero hero) {
        if (hero.isImmuneToAllSpells()) {
            return;
        }
        setRemainingTime(0);
        if (isContinuous()) {
            setDuration(-1);
        }
        if (!isPoison) {
            if (getDamageType().equals(EffectType.HEALTH)) {
                hero.addHealthPointInGame(damage);
            } else if (getDamageType().equals(EffectType.ATTACK_POWER)) {
                hero.addAttackPowerInGame(damage);
            }
        }
    }
}
