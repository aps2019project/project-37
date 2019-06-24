package com.ap.duelyst.model.items;

import com.ap.duelyst.model.cards.ActivationTime;
import com.ap.duelyst.model.buffs.Buff;
import com.ap.duelyst.view.card.CardSprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Item implements Cloneable {
    private String id;
    private String name;
    private String desc;
    private List<Buff> buffs = new ArrayList<>();
    private ActivationTime activationTime;
    private CardSprite cardSprite;
    private String fileName;

    public Item(String name, String desc, ActivationTime activationTime, Buff... buffs) {
        this.name = name;
        this.desc = desc;
        this.buffs.addAll(Arrays.asList(buffs));
        this.activationTime = activationTime;
    }

    @Override
    public Item clone() throws CloneNotSupportedException {
        Item item = (Item) super.clone();
        item.buffs = item.buffs.stream().map(buff -> {
            try {
                return buff.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return buff;
            }
        }).collect(Collectors.toList());
        return item;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean idEquals(String id) {
        if (getId().equals(id)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean nameEquals(String name) {
        if (getName().equals(name)) {
            return true;
        } else {
            return false;
        }
    }

    public String getInfo() {
        return "Name : " + getName() + " Desc : " + getDesc();
    }

    public ActivationTime getActivationTime() {
        return activationTime;
    }

    public List<Buff> getBuffs() {
        return buffs;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public CardSprite getCardSprite() {
        return cardSprite;
    }

    public void makeSprite() {
        this.cardSprite = new CardSprite(fileName);
    }
}
