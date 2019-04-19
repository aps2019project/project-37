package model;

public class DisarmBuff extends Buff {
    DisarmBuff(int duration){
        super(duration);
    }
    @Override
    public void applyBuff(Hero hero) {
        hero.setArmed(false);
    }
}
