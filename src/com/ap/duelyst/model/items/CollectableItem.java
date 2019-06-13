package com.ap.duelyst.model.items;

import com.ap.duelyst.model.cards.ActivationTime;
import com.ap.duelyst.model.buffs.Buff;

public class CollectableItem extends Item {


    public CollectableItem(String name, String desc, ActivationTime activationTime, Buff... buffs) {
        super(name, desc,activationTime,buffs);
    }

}
