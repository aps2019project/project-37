package model;

public class PowerBuff extends Buff {
    private int powerIncrease;

    PowerBuff(int duration , boolean continuous, int powerIncrease){
        super(duration, continuous);
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
