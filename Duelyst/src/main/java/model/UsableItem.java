package model;

public class UsableItem extends Item {
    private long price;
    public UsableItem(String name, String desc, long price){
        super(name, desc);
        setPrice(price);
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
