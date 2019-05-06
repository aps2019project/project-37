package model;

import controller.GameException;
import model.cards.Card;
import model.items.Item;
import model.items.UsableItem;

import java.util.ArrayList;
import java.util.List;

public class Collection extends Shop {


    public Collection() {
        super(new ArrayList<>(), new ArrayList<>());
    }

    public String getIdsByName(String name) {
        StringBuilder ids = new StringBuilder();
        getCards()
                .stream()
                .filter(card -> card.nameEquals(name))
                .forEach(card -> ids.append(card.getId()).append(", "));
        getUsableItems()
                .stream()
                .filter(usableItem -> usableItem.nameEquals(name))
                .forEach(usableItem -> ids.append(usableItem.getId()).append(", "));
        if (!ids.toString().isEmpty()) {
            return ids.toString();
        } else {
            throw new GameException("No Card or item with this name");
        }
    }

    public void removeById(String id) {
        Object object = getObjectById(id);
        if (object instanceof Card) {
            remove((Card) object);
        } else if (object instanceof UsableItem) {
            remove((UsableItem) object);
        }
    }

    public boolean hasById(String id) {
        if (hasCardById(id) | hasUsableItemById(id)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasCardById(String id) {
        return getCardById(id) != null;
    }

    public boolean hasUsableItemById(String id) {
        return getUsableItemById(id) != null;
    }

    public Object getObjectById(String id) {
        List<Object> cardsAndItems = new ArrayList<>();
        cardsAndItems.addAll(getCards());
        cardsAndItems.addAll(getItems());
        return cardsAndItems.stream().filter(o -> {
            if (o instanceof Item) {
                return ((Item) o).idEquals(id);
            } else {
                return ((Card) o).idEquals(id);
            }
        }).findFirst().orElseThrow(()->new GameException("Card/item not found"));
    }

    private Card getCardById(String id) {
        return getCards()
                .stream()
                .filter(card -> card.idEquals(id))
                .findFirst()
                .orElse(null);
    }

    private UsableItem getUsableItemById(String id) {
        return getUsableItems()
                .stream()
                .filter(usableItem -> usableItem.idEquals(id))
                .findFirst()
                .orElse(null);
    }

    public void add(UsableItem usableItem) {
        if (getItems().size() < 3) {
            super.add(usableItem);
        } else {
            throw new GameException("You have 3 Items in your Collection!");
        }
    }
}
