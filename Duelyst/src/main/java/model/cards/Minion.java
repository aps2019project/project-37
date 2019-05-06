package model.cards;

public class Minion extends Hero{
    private int mana;
    private String info;

    public Minion(String name, long price, int healthPoint, int attackPower,
           Spell specialPower, AttackType attackType, int range, int mana){
        super(name, price, healthPoint, attackPower, specialPower, attackType, range);
        setMana(mana);
        makeInfo();
    }

    @Override
    public Minion clone() throws CloneNotSupportedException {
        return (Minion) super.clone();
    }

    public int getMana(){
        return mana;
    }
    public void setMana(int mana) {
        this.mana = mana;
    }
    public void makeInfo(){
        info  = "Type : Minion" + " - Name : " + getName() ;
        info += " - Class : " + getAttackType().toString() + " - AP : " + getAttackPower();
        info += " - HP : " + getHealthPoint() + " - MP : " + getMana();
        info += " - Special Power : "+ getSpecialPower().getDesc();
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
