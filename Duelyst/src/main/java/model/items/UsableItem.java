package model.items;

import model.Account;
import model.buffs.Buff;
import model.cards.ActivationTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UsableItem extends Item {
    private long price;
    private String accountName;
    private List<Buff> buffs = new ArrayList<>();
    private ActivationTime activationTime;

    public UsableItem(String name, long price, String desc, ActivationTime activationTime, Buff... buffs) {
        super(name, desc);
        setPrice(price);
        this.activationTime = activationTime;
        this.buffs.addAll(Arrays.asList(buffs));
    }

    @SuppressWarnings("Duplicates")
    @Override
    public UsableItem clone() throws CloneNotSupportedException {
        UsableItem item = (UsableItem) super.clone();
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

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getInfoWithPrice() {
        return getInfo() + " Sell Price : " + getPrice();
    }

    public List<Buff> getBuffs() {
        return buffs;
    }

    public ActivationTime getActivationTime() {
        return activationTime;
    }
}
