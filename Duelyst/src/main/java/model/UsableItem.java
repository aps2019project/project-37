package model;

public class UsableItem extends Item {
    private long price;
    private Account account;
    public UsableItem(String name, String desc, long price){
        super(name, desc);
        setPrice(price);
    }
    public void setAccount(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
    @Override
    public UsableItem clone() throws CloneNotSupportedException {
        return (UsableItem) super.clone();
    }
    public long getPrice() {
        return price;
    }
    public void setPrice(long price) {
        this.price = price;
    }
    public String getInfoWithPrice() {
        return getInfo() + " Sell Price : "+getPrice();
    }
}
