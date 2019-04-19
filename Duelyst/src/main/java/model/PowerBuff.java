package model;

public class PowerBuff extends Buff {
    private int powerIncrease;
    PowerBuff(int duration ,int powerIncrease){
        super(duration);
        setPowerIncrease(powerIncrease);
    }
    @Override
    public void applyBuff(Hero hero) {
        hero.addAttackPower(getPowerIncrease());
    }

    public int getPowerIncrease() {
        return powerIncrease;
    }

    public void setPowerIncrease(int powerIncrease) {
        this.powerIncrease = powerIncrease;
    }
}
