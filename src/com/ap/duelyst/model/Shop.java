package com.ap.duelyst.model;

import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.items.CollectableItem;
import com.ap.duelyst.model.items.Item;
import com.ap.duelyst.model.items.UsableItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Shop {
    private List<Card> cards;
    private List<Item> items;


    public Shop(List<Card> cards, List<Item> items) {
        this.cards = cards;
        this.items = items;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<CollectableItem> getCollectableItems() {
        return items.stream().filter(item -> item instanceof CollectableItem)
                .map(item -> (CollectableItem) item)
                .collect(Collectors.toList());
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }


    public void add(Card card) {
        cards.add(card);
    }

    public void add(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    public void remove(Card card) {
        cards.remove(card);
    }

    public void remove(Item item) {
        items.remove(item);
    }

    public ArrayList<UsableItem> getUsableItems() {
        ArrayList<UsableItem> usableItems = new ArrayList<>();
        for (Item item : getItems()) {
            if (item instanceof UsableItem) {
                usableItems.add((UsableItem) item);
            }
        }
        return usableItems;
    }

    public ArrayList<Hero> getHeroes() {
        ArrayList<Hero> heroes = new ArrayList<>();
        for (Card card : getCards()) {
            if (card.getClass() == Hero.class) {
                heroes.add((Hero) card);
            }
        }
        return heroes;
    }

    public List<Hero> getHeroMinions() {
        ArrayList<Hero> heroes = new ArrayList<>();
        for (Card card : getCards()) {
            if (card instanceof Hero) {
                heroes.add((Hero) card);
            }
        }
        return heroes;
    }

    public Object getObjectByName(String name) {
        List<Object> cardsAndItems = new ArrayList<>();
        cardsAndItems.addAll(cards);
        cardsAndItems.addAll(getUsableItems());
        return cardsAndItems.stream().filter(o -> {
            if (o instanceof Item) {
                return ((Item) o).nameEquals(name);
            } else {
                return ((Card) o).nameEquals(name);
            }
        }).findFirst().orElseThrow(() -> new GameException("Card/item not found"));
    }

    private Card getCardByName(String name) {
        return getCards().stream()
                .filter(card -> card.nameEquals(name))
                .findFirst()
                .orElse(null);
    }

    private UsableItem getUsableItemByName(String name) {
        return getUsableItems().stream()
                .filter(usableItem -> usableItem.nameEquals(name))
                .findFirst()
                .orElse(null);
    }

    public String getHeroId(String name) {
        int index = 0;
        for (int i = 0; i < getHeroes().size(); i++) {
            if (getHeroes().get(i).nameEquals(name)) {
                index = i + 1;
                break;
            }
        }
        return "shop_hero_" + index;
    }

    public String getCardId(String name) {
        int index = 0;
        for (int i = 0; i < getCards().size(); i++) {
            if (getCards().get(i).nameEquals(name)) {
                index = i + 1;
                break;
            }
        }
        return "shop_card_" + index;
    }

    public String getItemId(String name) {
        int index = 0;
        for (int i = 0; i < getUsableItems().size(); i++) {
            if (getUsableItems().get(i).nameEquals(name)) {
                index = i + 1;
                break;
            }
        }
        return "shop_item_" + index;
    }

    public String getInfo() {
        return getInfo("");
    }

    public String getInfo(String tab) {
        StringBuilder info = new StringBuilder();

        info.append(tab).append("Heroes :\n");
        if (!getHeroes().isEmpty()) {
            info.append(getInfoOfHeroes());
        }

        info.append(tab).append("Items :\n");
        if (!getUsableItems().isEmpty()) {
            info.append(getInfoOfUsableItems());
        }

        info.append(tab).append("Cards:\n");
        if (!getCards().isEmpty()) {
            info.append(getInfoOfCards());
        }
        return info.toString();
    }

    public String getInfoOfHeroes() {
        StringBuilder info = new StringBuilder();
        if (!getHeroes().isEmpty()) {
            for (int i = 0; i < getHeroes().size(); i++) {
                info.append("\t").append(i + 1).append(" : ")
                        .append(getHeroes().get(i).getInfoWithPrice()).append("\n");
            }
        }
        return info.toString();
    }

    public String getInfoOfUsableItems() {
        StringBuilder info = new StringBuilder();
        ArrayList<UsableItem> usableItems = getUsableItems();

        if (!usableItems.isEmpty()) {
            for (int i = 0; i < usableItems.size(); i++) {
                UsableItem usableItem = usableItems.get(i);
                info.append("\t").append(i + 1).append(" : ")
                        .append(usableItem.getInfoWithPrice()).append("\n");
            }
        }

        return info.toString();
    }

    public String getInfoOfCards() {
        StringBuilder info = new StringBuilder();
        if (!getCards().isEmpty()) {
            for (int i = 0; i < getCards().size(); i++) {
                if (getCards().get(i).getClass() != Hero.class) {
                    info.append("\t").append(i + 1).append(" : ")
                            .append(getCards().get(i).getInfoWithPrice()).append("\n");
                }
            }
        }
        return info.toString();
    }
}
