package model;

public class WeaknessBuff extends Buff {
    private EffectType type;
    private int damage;

    WeaknessBuff(int duration , boolean continuous, EffectType type, int damage){
        super(duration, continuous);
        setType(type);
        setDamage(damage);
    }

    @Override
    public void applyBuff(Hero hero) {
        if(getDamageType().equals(EffectType.HEALTH)){
            hero.decreaseHealthPoint(damage);
        }
        else if(getDamageType().equals(EffectType.POWER)){
            hero.decreaseAttackPower(damage);
        }
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
}
