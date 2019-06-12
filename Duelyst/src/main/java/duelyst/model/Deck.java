package duelyst.model;

import duelyst.controller.Constants;
import duelyst.controller.GameException;
import duelyst.model.cards.Card;
import duelyst.model.cards.Hero;
import duelyst.model.items.UsableItem;

import java.util.ArrayList;

public class Deck extends Collection {
    private String name;

    public Deck(String name) {
        super();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasOneHero() {
        return getHeroes().size() == 1;
    }

    public Hero getHero() {
        return getHeroes().stream().findFirst().orElse(null);
    }


    @Override
    public String getInfoOfHeroes() {
        StringBuilder info = new StringBuilder();
        if (!getHeroes().isEmpty()) {
            for (int i = 0; i < getHeroes().size(); i++) {
                info.append("\t").append(i + 1).append(" : ")
                        .append(getHeroes().get(i).getInfoWithoutPrice()).append("\n");
            }
        }
        return info.toString();
    }

    @Override
    public String getInfoOfUsableItems() {
        StringBuilder info = new StringBuilder();
        ArrayList<UsableItem> usableItems = getUsableItems();

        if (!usableItems.isEmpty()) {
            for (int i = 0; i < usableItems.size(); i++) {
                UsableItem usableItem = usableItems.get(i);
                info.append("\t").append(i + 1).append(" : ")
                        .append(usableItem.getInfo()).append("\n");
            }
        }

        return info.toString();
    }

    @Override
    public String getInfoOfCards() {
        StringBuilder info = new StringBuilder();
        if (!getCards().isEmpty()) {
            for (int i = 0; i < getCards().size(); i++) {
                info.append("\t").append(i + 1).append(" : ")
                        .append(getCards().get(i).getInfoWithoutPrice()).append("\n");
            }
        }
        return info.toString();
    }

    public boolean isValid() {
        return getHeroes().size() == 1 && getCards().size() == 21 && getUsableItems().size() <= 1;
    }

    public boolean nameEquals(String name) {
        return getName().equals(name);
    }

    @Override
    public void add(Card card) {
        if (!hasCardById(card.getId())) {
            if (getCards().size() - getHeroes().size() < Constants.MAXIMUM_NUMBER_OF_CARDS_IN_DECK) {
                if (card.getClass().equals(Hero.class) && hasOneHero()) {
                    throw new GameException("Deck has one Hero!");
                } else {
                    super.add(card);
                }
            } else {
                throw new GameException("Deck has 20 cards!");
            }
        } else {
            throw new GameException("Deck has a card with this id!");
        }
    }

    @Override
    public void add(UsableItem usableItem) {
        if (!hasUsableItemById(usableItem.getId())) {
            super.add(usableItem);
        } else {
            throw new GameException("Deck has an item with this id!");
        }
    }
}
