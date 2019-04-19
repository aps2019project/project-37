package model;

public class Minion extends Hero{
    private int mana;
    private String info;
    {
        info  = "Type : Minion" + " - Name : " + getName() ;
        info += " - Class : " + getAttackType().toString() + " - AP : " + getAttackPower();
        info += " - HP : " + getHealthPoint() + " - MP : " + getMana();
        info += " - Special Power : "+ getSpecialPower().getDesc();
    }
    Minion(long id, String name, long price, int healthPoint,
           int attackPower, Spell specialPower, Type attackType,
           int range, int mana){

        super(id, name, price, healthPoint, attackPower, specialPower, attackType, range);
        setMana(mana);

    }

    public int getMana(){
        return mana;
    }
    public void setMana(int mana) {
        this.mana = mana;
    }
    @Override
    void showWithoutPrice() {
        System.out.println(info);
    }

    @Override
    void showWithPrice() {
        System.out.println(info + " Sell Cost : " + getPrice());
    }
}
