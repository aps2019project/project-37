package model;

public class Usable extends Item {
    private long price;
    Usable(long id, String name, String desc, long price){
        super(id, name, desc);
        setPrice(price);
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
