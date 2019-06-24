package com.ap.duelyst.view;

import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;

import java.util.List;

public interface GameEvents {
    void nextRound(List<Hero> inGameCards);

    void AIMove(Card card, int oldX, int oldY, int finalI, int finalJ);

    void gameEnded(String result);
}
