package model;

import java.util.ArrayList;

public class Collection {
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
        for(Card card:cards){
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
    public ArrayList<String> getIdsByName(String name){
        ArrayList<String> ids = new ArrayList<>();
        if(hasCardName(name)){
            for(Card card:getCards()){
                if(card.nameEquals(name)){
                    ids.add(card.getId());
                }
            }
        }
        if(hasItemName(name)){
            for(Item item:getItems()){
                if(item.nameEquals(name)){
                    ids.add(item.getName());
                }
            }
        }
        return ids;
    }
    public void remove(String id){
        if(hasCardId(id)){
            remove(getCard(id));
        }else if(hasItemId(id)){
            remove(getItem(id));
        }
    }
    public Card getCard(String id){
        for(Card card:getCards()){
            if(card.idEquals(id)){
                return card;
            }
        }
        return null;
    }
    public Item getItem(String id){
        for(Item item:getItems()){
            if(item.idEquals(id)){
                return item;
            }
        }
        return null;
    }
    public boolean hasId(String id){
        if(hasCardId(id) | hasItemId(id)){
            return true;
        }else {
            return false;
        }
    }
    public boolean hasCardId(String id){
        for(Card card:getCards()){
            if(card.idEquals(id)){
                return true;
            }
        }
        return false;
    }
    public boolean hasItemId(String id){
        for(Item item:getItems()){
            if(item.idEquals(id)){
                return true;
            }
        }
        return false;
    }
    public boolean hasName(String name){
        if(hasCardName(name) | hasItemName(name)){
            return true;
        }else {
            return false;
        }
    }
    public boolean hasCardName(String name){
        for(Card card:getCards()){
            if(card.nameEquals(name)){
                return true;
            }
        }
        return false;
    }
    public boolean hasItemName(String name){
        for(Item item:getItems()){
            if(item.nameEquals(name)){
                return true;
            }
        }
        return false;
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
}
