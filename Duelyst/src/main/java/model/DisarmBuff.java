package model;

public class DisarmBuff extends Buff {
    DisarmBuff(int duration, boolean continuous){
        super(duration, continuous);
    }
    @Override
    public void applyBuff(Hero hero) {
        hero.setArmed(false);
    }
}
