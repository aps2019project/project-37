package model.cards;

import model.buffs.Buff;

import java.util.ArrayList;

public class Spell extends Card{
    private ArrayList<Buff> effects = new ArrayList<>();
    private int mana;
    private String desc;
    private String info;

    public Spell(String name, long price, ArrayList<Buff> effects,
                 int mana, String desc){
        super(name, price);
        setEffects(effects);
        setMana(mana);
        setDesc(desc);
        makeInfo();
    }

    @Override
    public Spell clone() throws CloneNotSupportedException {
        Spell spell = (Spell) super.clone();
        spell.setEffects(new ArrayList<>());
        for(Buff effect : effects) {
            spell.addBuff(effect.clone());
        }
        return spell;
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
    public ArrayList<Buff> getEffects() {
        return effects;
    }
    public int getMana() {
        return mana;
    }
    public String getDesc(){
        return desc;
    }
    private void addBuff(Buff effect){
        effects.add(effect);
    }
    public void makeInfo(){
        info = "Type : Spell - Name : " + getName();
        info += " - MP : " + getMana() + " Description : " + getDesc();
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
