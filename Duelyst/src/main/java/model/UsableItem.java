package model;

public class UsableItem extends Item {
    private long price;

    UsableItem(String name, String desc, long price){
        super(name, desc);
        setPrice(price);
    }
    UsableItem(String id, UsableItem usable){
        super(id, usable.getName(), usable.getDesc());
        setPrice(usable.getPrice());
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
