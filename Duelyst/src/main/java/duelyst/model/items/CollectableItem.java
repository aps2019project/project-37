package duelyst.model.items;

import duelyst.model.buffs.Buff;
import duelyst.model.cards.ActivationTime;

public class CollectableItem extends Item {


    public CollectableItem(String name, String desc, ActivationTime activationTime, Buff... buffs) {
        super(name, desc,activationTime,buffs);
    }

}
