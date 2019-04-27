package model;

public class HolyBuff extends Buff {
    private int holyNumber;
    HolyBuff(int duration ,boolean continuous,int holyNumber){
        super(duration, continuous);
        setHolyNumber(holyNumber);
    }
    @Override
    public void applyBuff(Hero hero) {
        hero.addHolyNumber(getHolyNumber());
    }
    public int getHolyNumber() {
        return holyNumber;
    }

    public void setHolyNumber(int holyNumber) {
        this.holyNumber = holyNumber;
    }
}