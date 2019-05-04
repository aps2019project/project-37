package model.game;

import controller.Constants;
import controller.GameException;
import model.*;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    private Account account;
    private Hero hero;
    private Deck deck;
    private ArrayList<Card> hand = new ArrayList<>();
    private ArrayList<CollectableItem> collectableItems = new ArrayList<>();
    private int mana;
    private GraveYard graveYard = new GraveYard();
    private int indexOfNextCard;
    private boolean AI;

    Player(Account account, Deck deck, boolean AI){
        this.account = account;
        deck.remove(deck.getHero());
        this.deck = deck;
        this.AI = AI;
        initHand();
    }

    public Deck getDeck() {
        return deck;
    }

    public Account getAccount() {
        return account;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }
    public ArrayList<CollectableItem> getCollectableItems() {
        return collectableItems;
    }
    public int getMana() {
        return mana;
    }
    public GraveYard getGraveYard() {
        return graveYard;
    }
    public void setMana(int mana) {
        this.mana = mana;
    }
    public boolean isAI(){
        return AI;
    }
    public void decreaseMana(int mana){
        if(this.mana >= mana){
            this.mana -= mana;
        }
        throw new GameException("Not enough mana!");
    }
    public void increaseMana(int mana) {
        this.mana += mana;
    }
    public void initHand(){
        for(int i = 0; i < Constants.SIZE_OF_HAND; i++){
            hand.add(deck.getCards().get(i));
        }
    }
    public void addNextCardFromDeckToHand(){
        if(indexOfNextCard < Constants.MAXIMUM_NUMBER_OF_CARDS_IN_DECK - 1){
            hand.add(deck.getCards().get(indexOfNextCard));
            indexOfNextCard++;
        }
    }
    public Card getNextCardFromDeck(){
        if(indexOfNextCard < Constants.MAXIMUM_NUMBER_OF_CARDS_IN_DECK - 1){
            return deck.getCards().get(indexOfNextCard);
        }
        throw new GameException("No card is available to be added to your hand!");
    }
    public void addCollectableItem(CollectableItem collectableItem){
        collectableItems.add(collectableItem);
    }
    public void removeCollectableItem(CollectableItem collectableItem){
        collectableItems.remove(collectableItem);
    }
}
