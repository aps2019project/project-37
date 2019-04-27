package model;

import java.util.ArrayList;

public class Account {
    private String userName;
    private String password;
    private Collection collection;
    private ArrayList<Deck> decks;
    private Deck mainDeck;

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setCollection(Collection collection) {
        this.collection = collection;
    }
    public void setDecks(ArrayList<Deck> decks) {
        this.decks = decks;
    }
    public void setMainDeck(Deck mainDeck) {
        this.mainDeck = mainDeck;
    }
    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }
    public Collection getCollection() {
        return collection;
    }
    public ArrayList<Deck> getDecks() {
        return decks;
    }
    public Deck getMainDeck() {
        return mainDeck;
    }
    public void createDeck(String name){
        Deck deck = new Deck(name);
        decks.add(deck);
    }
    public boolean hasDeck(String name){
        if(getDeck(name).equals(null)){
            return false;
        }else{
            return true;
        }
    }
    public Deck getDeck(String name){
        for(Deck deck:getDecks()){
            if(deck.nameEquals(name)){
                return deck;
            }
        }
        return null;
    }
    public void removeDeck(String name){
        decks.remove(getDeck(name));
    }
    public void selectAsMainDeck(String name){
        Deck deck = getDeck(name);
        setMainDeck(deck);
    }
}
