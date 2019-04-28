package model;

import java.util.ArrayList;

public class Shop {
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();

    public ArrayList<Card> getCards() {
        return cards;
    }
    public ArrayList<Item> getItems() {
        return items;
    }
    public ArrayList<Hero> getHeroes() {
        ArrayList<Hero> heroes = new ArrayList<>();
        for(Card card:getCards()){
            if(card instanceof Hero){
                heroes.add((Hero)card);
            }
        }
        return heroes;
    }
    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
    public boolean hasByName(String name){
        if(hasCardByName(name) | hasItemByName(name)){
            return true;
        }else {
            return false;
        }
    }
    public boolean hasCardByName(String name){
        if(getCardByName(name).equals(null)){
            return false;
        }
        return true;
    }
    public boolean hasItemByName(String name){
        if(getItemByName(name).equals(null)){
            return false;
        }
        return true;
    }
    public Card getCardByName(String name){
        for(Card card:getCards()){
            if(card.nameEquals(name)){
                return card;
            }
        }
        return null;
    }
    public Item getItemByName(String name){
        for(Item item:getItems()){
            if(item.nameEquals(name)){
                return item;
            }
        }
        return null;
    }
    public void add(Card card){
        cards.add(card);
    }
    public void add(Item item){
        items.add(item);
    }
    public void remove(Card card){
        cards.remove(card);
    }
    public void remove(Item item){
        items.remove(item);
    }
    public String getInfo(){
        StringBuilder info = new StringBuilder();

        info.append("Heroes :\n");
        if(!getHeroes().isEmpty()){
            info.append(getInfoOfHeroes());
        }

        info.append("Items :\n");
        if(!getItems().isEmpty()){
            info.append(getInfoOfItems());
        }

        info.append("Cards:\n");
        if(!getCards().isEmpty()){
            info.append(getInfoOfCards());
        }
        return info.toString();
    }
    public String getInfoOfHeroes(){
        StringBuilder info = new StringBuilder();
        if(!getHeroes().isEmpty()){
            for(int i = 0; i < getHeroes().size(); i++){
                info.append("\t" + (i+1) + " : " + getHeroes().get(i).getInfoWithPrice() + "\n");
            }
        }
        return info.toString();
    }
    public String getInfoOfItems(){
        StringBuilder info = new StringBuilder();

        if(!getItems().isEmpty()){
            for(int i = 0; i < getItems().size(); i++){
                Item item = getItems().get(i);
                if(item instanceof UsableItem){
                    UsableItem usableItem = (UsableItem) item;
                    info.append("\t" + (i+1) + " : " + usableItem.getInfoWithPrice() + "\n");
                }
                else
                {
                    info.append("\t" + (i+1) + " : " + item.getInfo() + "\n");
                }
            }
        }

        return info.toString();
    }
    public String getInfoOfCards(){
        StringBuilder info = new StringBuilder();
        if(!getCards().isEmpty()){
            for(int i = 0; i < getCards().size(); i++){
                info.append("\t" + (i+1) + " : " + getCards().get(i).getInfoWithPrice() + "\n");
            }
        }
        return info.toString();
    }
}
