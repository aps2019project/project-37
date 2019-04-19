package model;

public class WeaknessBuff extends Buff {
    private DamageType damageType;
    private int damage;

    WeaknessBuff(int duration , boolean continuous, DamageType damageType, int damage){
        super(duration, continuous);
        setDamageType(damageType);
        setDamage(damage);
    }

    @Override
    public void applyBuff(Hero hero) {
        if(getDamageType().equals(DamageType.HEALTH)){
            hero.decreaseHealthPoint(damage);
        }
        else if(getDamageType().equals(DamageType.POWER)){
            hero.decreaseAttackPower(damage);
        }
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
