package model;

import java.util.ArrayList;

public class Account implements Comparable{
    private String userName;
    private String password;
    private int wins;
    private Collection collection;
    private ArrayList<Deck> decks;
    private Deck mainDeck;
    private long budget;

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
    public void setWins(int wins) {
        this.wins = wins;
    }
    public void setBudget(long budget) {
        this.budget = budget;
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
    public int getWins() {
        return wins;
    }
    public long getBudget() {
        return budget;
    }
    public void decreaseBudget(long money){
        budget -= money;
    }
    public void increaseBudget(long money){
        budget += money;
    }
    public void createDeck(String name){
        Deck deck = new Deck(name);
        decks.add(deck);
    }
    public boolean hasDeck(String name){
        if(getDeck(name)== null){
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
    public String getInfo(){
        return "UserName : " + userName + " - Wins : " + wins;
    }
    public boolean userNameEquals(String Name){
        if(this.userName.equals(Name)){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public int compareTo(Object object) {
        Account account = (Account) object;
        return account.wins - this.wins;
    }

}
