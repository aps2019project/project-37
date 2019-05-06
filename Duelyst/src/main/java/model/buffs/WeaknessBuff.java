package model.buffs;

import model.buffs.traget.EffectType;
import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;

import java.util.List;

public class WeaknessBuff extends Buff {
    private EffectType type;
    private int damage;
    boolean isPoison;

    public WeaknessBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range,
                        EffectType type, int damage) {
        super(duration, continuous, target, side, range);
        this.type = type;
        this.damage = damage;
    }

    public EffectType getDamageType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public void setType(EffectType type) {
        this.type = type;
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
