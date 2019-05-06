package model.items;

import model.buffs.Buff;
import model.cards.ActivationTime;


public class UsableItem extends Item {
    private long price;
    private String accountName;

    public UsableItem(String name, long price, String desc, ActivationTime activationTime, Buff... buffs) {
        super(name, desc,activationTime, buffs);
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
}
