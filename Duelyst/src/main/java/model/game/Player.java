package model.game;

import controller.Constants;
import controller.GameException;
import model.*;
import model.buffs.Buff;
import model.buffs.ManaBuff;
import model.cards.Card;
import model.cards.Hero;
import model.items.CollectableItem;
import model.items.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private String accountName;
    private Hero hero;
    private Deck deck;
    private List<Card> hand = new ArrayList<>();
    private List<CollectableItem> collectableItems = new ArrayList<>();
    private List<ManaBuff> buffs = new ArrayList<>();
    private int mana;
    private int indexOfNextCard;
    private boolean AI;
    private int flagTime;

    Player(String accountName, Deck deck, boolean AI) {
        this.accountName = accountName;
        this.deck = deck;
        hero = this.deck.getHero();
        this.deck.remove(hero);
        Collections.shuffle(this.deck.getCards());
        this.AI = AI;
    }

    public Item startGame() {
        if (hero.isPassive()) {
            hero.getInGame().addBuff(hero.getSpecialPower().getEffects().get(0));
        }
        List<Item> items = deck.getItems();
        if (items != null && !items.isEmpty()) {
            return items.get(0);
        } else {
            return null;
        }
    }

    public void nextRound(int round) {
        if (round <= 14) {
            if (round % 2 != 0) {
                int turn = (round + 1) / 2;
                mana = turn + 1;
            } else {
                int turn = round / 2;
                mana = turn + 2;
            }
        } else {
            mana = 9;
        }
        for (ManaBuff buff : buffs) {
            if (buff.getRemainingTime() > 0) {
                buff.decreaseRemainingTime();
                mana += buff.getAmount();
            }
        }
        List<Card> cards = new ArrayList<>(deck.getCards());
        cards.add(hero);
        cards.stream().filter(card -> card instanceof Hero).forEach(card -> {
            Hero hero = (Hero) card;
            hero.nextRound();
            for (Buff buff : hero.getInGame().getBuffs()) {
                if (buff.getDuration() > 0 && buff.getRemainingTime() <= 0) {
                    buff.inactivate(hero);
                }
                buff.applyBuff(hero);
            }
        });
        fillHand();
    }

    public Deck getDeck() {
        return deck;
    }

    public String getAccountName() {
        return accountName;
    }

    public List<Card> getHand() {
        return hand;
    }

    public Card getNextCard() {
        return deck.getCards().get(indexOfNextCard);
    }

    public List<CollectableItem> getCollectableItems() {
        return collectableItems;
    }

    public int getMana() {
        return mana;
    }

    public Hero getHero() {
        return hero;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public boolean isAI() {
        return AI;
    }

    public void decreaseMana(int mana) {
        if (this.mana >= mana) {
            this.mana -= mana;
        } else {
            throw new GameException("Not enough mana!");
        }
    }

    public void increaseMana(int mana) {
        this.mana += mana;
    }

    public void fillHand() {
        int size = hand.size();
        for (int i = size; i < Constants.SIZE_OF_HAND; i++) {
            addNextCardFromDeckToHand();
        }
    }

    public void addNextCardFromDeckToHand() {
        if (indexOfNextCard < Constants.MAXIMUM_NUMBER_OF_CARDS_IN_DECK) {
            hand.add(deck.getCards().get(indexOfNextCard));
            indexOfNextCard++;
        }
    }

    public String getPlayersWithFlag() {
        List<Card> cards = new ArrayList<>(deck.getCards());
        cards.add(hero);
        return cards.stream().filter(card -> card instanceof Hero)
                .filter(card -> ((Hero) card).getInGame().isHasFlag())
                .map(Card::getName)
                .collect(Collectors.toList()).toString();
    }

    public boolean hasCard(Card card) {
        return accountName.equals(card.getAccountName());
    }

    public void addCollectableItem(CollectableItem collectableItem) {
        collectableItems.add(collectableItem);
    }

    public void removeCollectableItem(CollectableItem collectableItem) {
        collectableItems.remove(collectableItem);
    }

    public void addManaBuff(ManaBuff manaBuff) {
        buffs.add(manaBuff);
    }

    public int getFlagTime() {
        return flagTime;
    }

    public void addFlagTime() {
        flagTime++;
    }
}
