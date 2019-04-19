package model;

public class PowerBuff extends Buff {
    private EffectType type;
    private int improve;

    PowerBuff(int duration , boolean continuous, EffectType type, int improve){
        super(duration, continuous);
        setImprove(improve);
        setType(type);
    }

    @Override
    public void applyBuff(Hero hero) {
        if(getType().equals(EffectType.HEALTH)){
            hero.addHealthPoint(improve);
        }
        else if(getType().equals(EffectType.POWER)){
            hero.addAttackPower(improve);
        }
    }

    public int getImprove() {
        return improve;
    }

    public EffectType getType(){
        return type;
    }

    public void setImprove(int improve) {
        this.improve = improve;
    }

    public void setType(EffectType type) {
        this.type = type;
    }
}
