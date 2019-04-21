package model;

abstract public class Card {
    private long id;
    private String inGameID;
    private String name;
    private long price;
    Card(long id, String name, long price){
        setId(id);
        setName(name);
        setPrice(price);
    }
    public void setId(long id){
        this.id = id;
    }
    public void setInGameID(String inGameID){
        this.inGameID = inGameID;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPrice(long price){
        this.price = price;
    }
    public long getId(){
        return id;
    }
    public String getInGameID() {
        return inGameID;
    }
    public String getName() {
        return name;
    }
    public long getPrice(){
        return price;
    }
    abstract String getInfoWithPrice();
    abstract String getInfoWithoutPrice();
}

