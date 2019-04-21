package model;

import java.util.ArrayList;

public class Collection {
    private ArrayList<Hero> heroes = new ArrayList<>();
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();


    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<Hero> getHeroes() {
        return heroes;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setHeroes(ArrayList<Hero> heroes) {
        this.heroes = heroes;
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
                if(item instanceof Usable){
                    Usable usableItem = (Usable) item;
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
    public boolean has(String name){
        if(hasHero(name)|hasItem(name)|hasCard(name)) {
            return true;
        }
        else{
            return false;
        }
    }
    public boolean hasHero(String name){
        if(getHero(name) != null){
            return true;
        }
        return false;
    }
    public boolean hasItem(String name){
        if(getItem(name) != null){
            return true;
        }
        return false;
    }
    public boolean hasCard(String name){
        if(getCard(name) != null){
            return true;
        }
        return false;
    }
    public long getId(String name){
        if(hasHero(name)){
            return getHero(name).getId();
        } else if(hasItem(name)) {
            return getItem(name).getId();
        } else if(hasCard(name)) {
            return getCard(name).getId();
        } else{
            return -1;
        }
    }
    public Hero getHero(String name){
        for(Hero hero:getHeroes()){
            if(hero.getName().equals(name)){
                return hero;
            }
        }
        return null;
    }
    public Item getItem(String name){
        for(Item item:getItems()){
            if(item.getName().equals(name)){
                return item;
            }
        }
        return null;
    }
    public Card getCard(String name){
        for(Card card:getCards()){
            if(card.getName().equals(name)){
                return card;
            }
        }
        return null;
    }
    public void addToCollection(Card card){
        cards.add(card);
    }
    public void addToCollection(Hero hero){
        heroes.add(hero);
    }
    public void addToCollection(Item item){
        items.add(item);
    }
    public void removeFromCollection(Card card){
        cards.remove(card);
    }
    public void removeFromCollection(Hero hero){
        heroes.remove(hero);
    }
    public void removeFromCollection(Item item){
        items.remove(item);
    }
}
