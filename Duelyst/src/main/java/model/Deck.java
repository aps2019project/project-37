package model;

import java.util.ArrayList;

public class Deck {
    private String name;
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();

    Deck(String name){
        setName(name);
    }

    public String getName() {
        return name;
    }
    public Hero getHero() {
        for(Card card:getCards()){
            if(card instanceof Hero){
                return (Hero) card;
            }
        }
        return null;
    }
    public ArrayList<Item> getItems() {
        return items;
    }
    public ArrayList<Card> getCards() {
        return cards;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
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
    public boolean hasId(String id){
        if(hasCardId(id) | hasItemId(id)){
            return true;
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
    public boolean hasCardId(String id){
        for(Card card:getCards()){
            if(card.idEquals(id)){
                return true;
            }
        }
        return false;
    }
    public boolean hasOneHero(){
        if(getHero().equals(null)){
            return false;
        }else{
            return true;
        }
    }
    public boolean nameEquals(String name){
        if(getName().equals(name)){
            return true;
        }else{
            return false;
        }
    }
}
