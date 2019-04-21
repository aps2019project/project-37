package model;

public class Minion extends Hero{
    private int mana;
    private String info;
    Minion(long id, String name, long price, int healthPoint,
           int attackPower, Spell specialPower, Type attackType,
           int range, int mana){

        super(id, name, price, healthPoint, attackPower, specialPower, attackType, range);
        setMana(mana);

        info  = "Type : Minion" + " - Name : " + getName() ;
        info += " - Class : " + getAttackType().toString() + " - AP : " + getAttackPower();
        info += " - HP : " + getHealthPoint() + " - MP : " + getMana();
        info += " - Special Power : "+ getSpecialPower().getDesc();

    }

    public int getMana(){
        return mana;
    }
    public void setMana(int mana) {
        this.mana = mana;
    }
    @Override
    public String getInfoWithoutPrice() {
         return info;
    }

    @Override
    public String getInfoWithPrice() {
        return info + " Sell Cost : " + getPrice();
    }
}
