package duelyst.model;

import duelyst.controller.GameException;
import duelyst.model.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Account {
    private String userName;
    private String password;
    private int wins;
    private Collection collection = new Collection();
    private ArrayList<Deck> decks = new ArrayList<>();
    private Deck mainDeck;
    private long budget = 15000;
    private List<MatchHistory> histories=new ArrayList<>();


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public void setDecks(ArrayList<Deck> decks) {
        this.decks = decks;
    }

    public void setMainDeck(Deck mainDeck) {
        this.mainDeck = mainDeck;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Collection getCollection() {
        return collection;
    }

    public ArrayList<Deck> getDecks() {
        return decks;
    }

    public Deck getMainDeck() {
        return mainDeck;
    }

    public Deck getCloneDeck() {
        Deck deck = new Deck(mainDeck.getName());
        List<Card> cards = mainDeck.getCards().stream().map(card -> {
            try {
                return card.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return card;
            }
        }).collect(Collectors.toList());
        deck.setCards(cards);
        try {
            if (mainDeck.getUsableItems().size() == 1) {
                deck.add(mainDeck.getUsableItems().get(0).clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return deck;
    }

    public int getWins() {
        return wins;
    }

    public long getBudget() {
        return budget;
    }

    public void decreaseBudget(long money) {
        if (budget >= money) {
            budget -= money;
        } else {
            throw new GameException("Not enough money!");
        }
    }

    public void increaseBudget(long money) {
        budget += money;
    }

    public void createDeck(String name) {
        if (!hasDeck(name)) {
            decks.add(new Deck(name));
        } else {
            throw new GameException("There is a deck with this name");
        }
    }

    public boolean hasDeck(String name) {
        return getDecks()
                .stream()
                .anyMatch(deck -> deck.nameEquals(name));
    }

    public Deck getDeck(String name) {
        return getDecks()
                .stream()
                .filter(deck -> deck.nameEquals(name))
                .findFirst()
                .orElseThrow(() -> new GameException("No deck with this name!"));
    }

    public void removeDeck(String name) {
        if (hasDeck(name)) {
            decks.remove(getDeck(name));
        } else {
            throw new GameException("No deck with this name!");
        }
    }

    public String showAllDecks() {
        StringBuilder info = new StringBuilder();
        if (mainDeck != null) {
            decks.remove(mainDeck);
            decks.add(0, mainDeck);
        }
        if (decks.isEmpty()) {
            throw new GameException("You dont have any decks");
        }
        for (int i = 0; i < decks.size(); i++) {
            info.append(i + 1).append(" : ")
                    .append(decks.get(i).getName()).append(" :\n")
                    .append(decks.get(i).getInfo("\t"));
        }
        return info.toString();
    }

    public void selectAsMainDeck(String name) {
        Deck deck = getDeck(name);
        setMainDeck(deck);
    }

    public String getInfo() {
        return "UserName : " + userName + " - Wins : " + wins;
    }

    public boolean userNameEquals(String Name) {
        return this.userName.equals(Name);
    }

    public List<MatchHistory> getHistories() {
        return histories;
    }

    public void addToHistory(MatchHistory history){
        histories.add(history);
    }
}

