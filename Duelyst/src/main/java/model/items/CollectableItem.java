package model.items;

import model.buffs.Buff;
import model.cards.ActivationTime;

public class CollectableItem extends Item {


    public CollectableItem(String name, String desc, ActivationTime activationTime, Buff... buffs) {
        super(name, desc,activationTime,buffs);
    }

}
