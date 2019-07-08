package com.ap.duelyst.view;

import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.items.Item;

import java.util.List;

public interface GameEvents {
    void nextRound(List<Hero> inGameCards);

    void gameEnded(String result);

    void insert(Card card, int x, int y);

    void move(Card card, int oldX, int oldY, int finalI, int finalJ);

    void attack(Card attacker, Card attacked);

    void specialPower(Card card, int finalI, int finalJ);

    void useCollectable(Item item);

    void startGame();
}
