package com.ap.duelyst.model.game;

import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.model.cards.Card;

import java.util.ArrayList;

public class GraveYard {
    private ArrayList<Card> cards = new ArrayList<>();

    public void add(Card card) {
        cards.add(card);
    }

    public String getInfoOfAllCards() {
        StringBuilder info = new StringBuilder();
        cards.forEach(card -> info.append(card.getInfoWithoutPrice()));
        return info.toString();
    }

    public String getInfo(String id) {
        Card card = cards.stream().filter(x -> x.idEquals(id)).findFirst()
                .orElseThrow(() -> new GameException("No card with this id!"));
        return card.getInfoWithoutPrice();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
}
