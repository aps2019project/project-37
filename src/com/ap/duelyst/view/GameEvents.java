package com.ap.duelyst.view;

import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;

import java.util.List;

public interface GameEvents {
    void nextRound(List<Hero> inGameCards);

    void gameEnded(String result);
}
