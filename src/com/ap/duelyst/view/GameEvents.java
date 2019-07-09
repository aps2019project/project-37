package com.ap.duelyst.view;

import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.items.Item;

import java.util.List;

public interface GameEvents {
    void nextRound();

    void gameEnded(String result);

    void insert(String cardId, int x, int y);

    void move(String id, int oldX, int oldY, int finalI, int finalJ);

    void attack(String attackerId, String attacked);

    void specialPower(String cardId, int finalI, int finalJ);

    void useCollectable(String itemId);

    void startGame();

    void error(String message);
}
