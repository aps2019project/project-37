package model;

import controller.GameException;

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
        if(budget >= money)
            budget -= money;
        else
            throw new GameException("Not enough money!");
    }
    public void increaseBudget(long money){
        budget += money;
    }
    public void createDeck(String name){
        if(!hasDeck(name)){
            decks.add(new Deck(name));
        }else {
            throw new GameException("There is a deck with this name");
        }
    }
    public boolean hasDeck(String name){
        return getDecks()
                .stream()
                .anyMatch(deck -> deck.nameEquals(name));
    }
    public Deck getDeck(String name){
        return getDecks()
                .stream()
                .filter(deck -> deck.nameEquals(name))
                .findFirst()
                .orElseThrow(() -> new GameException("No deck with this name!"));
    }
    public void removeDeck(String name){
        if(hasDeck(name)) {
            decks.remove(getDeck(name));
        }else {
            throw new GameException("No deck with this name!");
        }
    }
    public void selectAsMainDeck(String name){
        Deck deck = getDeck(name);
        setMainDeck(deck);
    }
    public String getInfo(){
        return "UserName : " + userName + " - Wins : " + wins;
    }
    public boolean userNameEquals(String Name){
        if(this.userName.equals(Name))
            return true;
        else
            return false;
    }

    @Override
    public int compareTo(Object object) {
        Account account = (Account) object;
        return account.wins - this.wins;
    }

}
