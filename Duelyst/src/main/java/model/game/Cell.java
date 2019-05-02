package model.game;

import model.Buff;
import model.CollectableItem;

public class Cell {
    private Player player;
    private Buff buff;
    private CollectableItem collectableItem;

    public Buff getBuff() {
        return buff;
    }
    public Player getPlayer() {
        return player;
    }
    public CollectableItem getCollectableItem() {
        return collectableItem;
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
}
