package model.buffs;

import model.buffs.traget.EffectType;
import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;

import java.util.List;

public class PowerBuff extends Buff {
    private EffectType type;
    private int improve;

    public PowerBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range,
                     EffectType type, int improve) {
        super(duration, continuous, target, side, range);
        this.type = type;
        this.improve = improve;
    }

    public int getImprove() {
        return improve;
    }

    public EffectType getType() {
        return type;
    }

    public void setImprove(int improve) {
        this.improve = improve;
    }

    public void setType(EffectType type) {
        this.type = type;
    }


    @Override
    public void applyBuff(List<Hero> heroes) {
        if (getDuration() == -1 || getRemainingTime() > 0) {
            for (Hero hero : heroes) {
                if (getType().equals(EffectType.HEALTH)) {
                    hero.addHealthPointInGame(improve);
                } else if (getType().equals(EffectType.ATTACK_POWER)) {
                    hero.addAttackPowerInGame(improve);
                }
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
        for (Hero hero : heroes) {
            if (getType().equals(EffectType.HEALTH)) {
                hero.addHealthPointInGame(-improve);
            } else if (getType().equals(EffectType.ATTACK_POWER)) {
                hero.addAttackPowerInGame(-improve);
            }
        }
    }
}
