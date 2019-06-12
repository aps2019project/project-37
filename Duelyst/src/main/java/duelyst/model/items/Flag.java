package duelyst.model.items;

import duelyst.model.buffs.Buff;
import duelyst.model.cards.ActivationTime;

public class Flag extends Item {

    public Flag(String name, String desc, ActivationTime activationTime, Buff... buffs) {
        super(name, desc, activationTime, buffs);
    }

    @Override
    public Flag clone() throws CloneNotSupportedException {
        return (Flag) super.clone();
    }
}
