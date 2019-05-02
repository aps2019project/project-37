package model;

abstract public class Card implements Cloneable{
    private String id;
    private String name;
    private long price;
    Card(String name, long price){
        setName(name);
        setPrice(price);
    }
    Card(String id, String name, long price){
        setId(id);
        setName(name);
        setPrice(price);
    }
    public Card clone()throws CloneNotSupportedException{
        return (Card) super.clone();
    }
    public void setId(String id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPrice(long price){
        this.price = price;
    }
    public String getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public long getPrice(){
        return price;
    }
    public boolean idEquals(String id){
        if(getId().equals(id)){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean nameEquals(String name){
        if(getName().equals(name)){
            return true;
        }
        else{
            return false;
        }
    }
    abstract String getInfoWithPrice();
    abstract String getInfoWithoutPrice();
}

