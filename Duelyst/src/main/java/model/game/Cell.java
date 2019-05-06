package model.game;

import model.buffs.Buff;
import model.Card;
import model.CollectableItem;

public class Cell {
    private Player player;
    private Buff buff;
    private CollectableItem collectableItem;
    private Card card;
    private int x;
    private int y;

    public Buff getBuff() {
        return buff;
    }
    public Player getPlayer() {
        return player;
    }
    public CollectableItem getCollectableItem() {
        return collectableItem;
    }
    public Card getCard() {
        return card;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setBuff(Buff buff) {
        this.buff = buff;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public void setCollectableItem(CollectableItem collectableItem) {
        this.collectableItem = collectableItem;
    }
    public void setCard(Card card) {
        this.card = card;
    }
    public void removeCard(){
        card = null;
    }
    public void removeCollectableItem(){collectableItem = null;}
    public boolean hasCard(){
        if(card != null){
            return true;
        }else {
            return false;
        }
    }
    public boolean hasCollectableItem(){
        if(collectableItem != null){
            return true;
        }else {
            return false;
        }
    }
}
