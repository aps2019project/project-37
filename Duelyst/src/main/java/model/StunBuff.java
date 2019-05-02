package model;

public class StunBuff extends Buff {
    StunBuff(int duration , boolean continuous){
        super(duration, continuous);
    }

    @Override
    public StunBuff clone() throws CloneNotSupportedException {
        return (StunBuff) super.clone();
    }

    @Override
    public void applyBuff(Hero hero) {
        hero.setMovable(false);
    }
}
