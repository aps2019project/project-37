package model.game;

import model.buffs.Buff;
import model.cards.Card;
import model.items.CollectableItem;

import java.util.List;

public class Cell {
    private Player player;
    private List<Buff> buffs;
    private CollectableItem collectableItem;
    private Card card;
    private int x;
    private int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public List<Buff> getBuffs() {
        return buffs;
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

    public void setBuff(List<Buff> buffs) {
        this.buffs = buffs;
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

    public void removeCard() {
        card = null;
    }

    public void removeCollectableItem() {
        collectableItem = null;
    }

    public boolean hasCard() {
        if (card != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasCollectableItem() {
        if (collectableItem != null) {
            return true;
        } else {
            return false;
        }
    }
}
