package model;

public class Usable extends Item {
    private long price;

    Usable(String name, String desc, long price){
        super(name, desc);
        setPrice(price);
    }
    Usable(String id, Usable usable){
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
