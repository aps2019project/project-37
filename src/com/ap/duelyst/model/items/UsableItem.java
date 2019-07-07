package com.ap.duelyst.model.items;

import com.ap.duelyst.model.cards.ActivationTime;
import com.ap.duelyst.model.buffs.Buff;


public class UsableItem extends Item {
    private long price;
    private String accountName;
    private int count = 10;

    public UsableItem(String name, long price, String desc,
                      ActivationTime activationTime, Buff... buffs) {
        super(name, desc, activationTime, buffs);
        setPrice(price);
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

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        count++;
    }

    public void decreaseCount() {
        count--;
    }
}
