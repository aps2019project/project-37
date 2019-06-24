package com.ap.duelyst.model.cards;

import com.ap.duelyst.model.buffs.Buff;
import com.ap.duelyst.view.card.CardSprite;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spell extends Card {
    private List<Buff> effects;
    private int mana;
    private String desc;
    private String info;
    private String effectFileName;

    public Spell(String name, long price,
                 int mana, String desc, Buff... effects) {
        super(name, price);
        this.effects = new ArrayList<>(Arrays.asList(effects));
        setMana(mana);
        setDesc(desc);
        makeInfo();
    }

    @Override
    public Spell clone() throws CloneNotSupportedException {
        Spell spell = (Spell) super.clone();
        spell.setEffects(new ArrayList<>());
        for (Buff effect : effects) {
            spell.addBuff(effect.clone());
        }
        return spell;
    }

    @Override
    public void makeCardSprite() {
        cardSprite = new CardSprite(getFileName(), effectFileName);
    }

    @Override
    public ImageView getImageView() {
        cardSprite.getImageView().setScaleX(2);
        cardSprite.getImageView().setScaleY(2);
        return cardSprite.getImageView();
    }

    public void setEffects(List<Buff> effects) {
        this.effects = effects;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Buff> getEffects() {
        return effects;
    }

    public int getMana() {
        return mana;
    }

    public String getDesc() {
        return desc;
    }

    private void addBuff(Buff effect) {
        effects.add(effect);
    }

    public void makeInfo() {
        info = "Type : Spell - Name : " + getName();
        info += " - MP : " + getMana() + " Description : " + getDesc();
    }

    @Override
    public String getInGameInfo() {
        return "Spell:\n" +
                "Name: " + getName() + "\n" +
                "MP: " + mana + "\n" +
                "Cost: " + getPrice() + "\n" +
                "Desc: " + desc + "\n";
    }

    @Override
    public String getInfoWithPrice() {
        return info;
    }

    @Override
    public String getInfoWithoutPrice() {

        return info + " - Sell Cost : " + getPrice();
    }

    public String getEffectFileName() {
        return effectFileName;
    }

    public void setEffectFileName(String effectFileName) {
        this.effectFileName = effectFileName;
    }
}
