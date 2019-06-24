package com.ap.duelyst.model.game;

import com.ap.duelyst.model.buffs.*;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.items.CollectableItem;

import java.util.*;

public class Cell {
    private Hero heroMinion;
    private List<Buff> buffs = new ArrayList<>();
    private CollectableItem collectableItem;
    private boolean hasFlag;
    private int x;
    private int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;

    }

    public CollectableItem addCard(Card card) {
        heroMinion = (Hero) card;
        heroMinion.setPosition(x, y);
        if (hasFlag) {
            heroMinion.getInGame().setHasFlag(true);
            hasFlag = false;
        }
        for (Buff buff : buffs) {
            CellBuff cellBuff = (CellBuff) buff;
            applyBuff(cellBuff);
        }
        CollectableItem item = collectableItem;
        collectableItem = null;
        return item;
    }

    public Hero getCard() {
        return heroMinion;
    }


    public void nextRound() {
        for (Iterator<Buff> iterator = buffs.iterator(); iterator.hasNext(); ) {
            Buff buff = iterator.next();
            buff.decreaseRemainingTime();
            CellBuff cellBuff = (CellBuff) buff;
            if (cellBuff.getBuff() instanceof WeaknessBuff) {
                if (heroMinion != null) {
                    if (!heroMinion.isImmuneToAllSpells()) {
                        heroMinion.getInGame().decreaseHealthPoint(2);
                    }
                }
            }
            if (buff.getRemainingTime() <= 0) {
                deactivate(buff);
                iterator.remove();
            }
        }
    }

    private void deactivate(Buff buff) {
        CellBuff cellBuff = (CellBuff) buff;
        if (cellBuff.getBuff() instanceof HolyBuff) {
            if (heroMinion != null) {
                heroMinion.getInGame().addHolyNumber(-1);
            }
        }
    }

    public void removeCard(boolean removeFlag) {
        heroMinion.setPosition(-1, -1);
        for (Buff buff : buffs) {
            CellBuff cellBuff = (CellBuff) buff;
            if (cellBuff.getBuff() instanceof HolyBuff) {
                heroMinion.getInGame().addHolyNumber(-1);
            }
        }
        if (removeFlag && heroMinion.getInGame().isHasFlag()) {
            hasFlag = true;
        }
        heroMinion = null;
    }

    public List<Buff> getBuffs() {
        return buffs;
    }

    public void addBuff(Buff buff) {
        try {
            CellBuff cellBuff = (CellBuff) buff.clone();
            applyBuff(cellBuff);
            this.buffs.add(cellBuff);
        } catch (CloneNotSupportedException ignored) {
        }
    }

    private void applyBuff(CellBuff cellBuff) {
        if (heroMinion != null) {
            try {
                Buff buff1 = cellBuff.getBuff().clone();
                if (cellBuff.getBuff() instanceof PoisonBuff) {
                    if (heroMinion.isImmuneToAllSpells() || !heroMinion.isCanBePoisoned()) {
                        return;
                    }
                    heroMinion.getInGame().addBuff(buff1);
                    buff1.applyBuff(heroMinion);
                } else if (cellBuff.getBuff() instanceof HolyBuff) {
                    heroMinion.getInGame().addHolyNumber(1);
                } else if (cellBuff.getBuff() instanceof WeaknessBuff) {
                    heroMinion.getInGame().addBuff(buff1);
                    buff1.applyBuff(heroMinion);
                }
            } catch (CloneNotSupportedException ignored) {
            }
        }
    }

    public Hero getHeroMinion() {
        return heroMinion;
    }

    public boolean isEmpty() {
        return heroMinion == null;
    }

    public CollectableItem getCollectableItem() {
        return collectableItem;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void setHeroMinion(Hero heroMinion) {
        this.heroMinion = heroMinion;
    }

    public void setCollectableItem(CollectableItem collectableItem) {
        this.collectableItem = collectableItem;
    }

    public boolean isHasFlag() {
        return hasFlag;
    }

    public void setHasFlag(boolean hasFlag) {
        this.hasFlag = hasFlag;
    }

}
