package model;

public class Usable extends Item {
    private long price;
    Usable(long id, String name, String desc, long price){
        super(id, name, desc);
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public void show() {
        System.out.println(("Name : " + getName() + " Desc : " + getDesc()) + " Sell Price : "+getPrice());
    }
}
