package model.items;

import model.buffs.Buff;
import model.cards.ActivationTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectableItem extends Item {


    private List<Buff> buffs = new ArrayList<>();
    private ActivationTime activationTime;

    public CollectableItem(String name, String desc, ActivationTime activationTime, Buff... buffs) {
        super(name, desc);
        this.activationTime = activationTime;
        this.buffs.addAll(Arrays.asList(buffs));
    }

    @SuppressWarnings("Duplicates")
    @Override
    public CollectableItem clone() throws CloneNotSupportedException {
        CollectableItem item = (CollectableItem) super.clone();
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


    public List<Buff> getBuffs() {
        return buffs;
    }

    public ActivationTime getActivationTime() {
        return activationTime;
    }
}
