package model;

import java.util.ArrayList;

public class Spell extends Card{
    private Target target;
    private ArrayList<Buff> effects = new ArrayList<>();
    private int mana;
    private String desc;
    private String info;

    Spell(long id, long price, String name, Target target,
          ArrayList<Buff> effects, int mana){
        super(id, name, price);
        setTarget(target);
        setEffects(effects);
        setMana(mana);
    }

    {
        info = "Type : Spell - Name : " + getName();
        info += " - MP : " + getMana() + " Description : " + getDesc();
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void setEffects(ArrayList<Buff> effects) {
        this.effects = effects;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }
    public Target getTarget() {
        return target;
    }

    public ArrayList<Buff> getEffect() {
        return effects;
    }

    public int getMana() {
        return mana;
    }

    public String getDesc(){
        return desc;
    }
    @Override
    public String getInfoWithPrice() {
        return info;
    }

    @Override
    public String getInfoWithoutPrice() {

        return info + " - Sell Cost : " + getPrice();
    }
}
