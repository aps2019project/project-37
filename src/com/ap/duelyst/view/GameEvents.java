package com.ap.duelyst.view;

import com.ap.duelyst.model.cards.Card;

import java.util.List;

public interface GameEvents {
    void insert(int x, int y);

    void nextRound(List<Card> hand, int p1Mana, int p2Mana, List<Card> inGameCards);

    void move(int x, int y);

}
