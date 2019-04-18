package model;

abstract public class Card {
    private long id;
    private String inGameID;
    private long price;
    Card(long id, String inGameID, long price){
        setId(id);
        setInGameID(inGameID);
        setPrice(price);
    }
    private void setId(long id){
        this.id = id;
    }
    private void setInGameID(String inGameID){
        this.inGameID = inGameID;
    }
    private void setPrice(long price){
        this.price = price;
    }
    public long getId(){
        return id;
    }
    public String getInGameID() {
        return inGameID;
    }
    public long getPrice(){
        return price;
    }
    abstract void showWithPrice();
    abstract void showWithoutPrice();
}

