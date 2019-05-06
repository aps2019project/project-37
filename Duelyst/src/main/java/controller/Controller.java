package controller;

import controller.menu.BattleMenu;
import controller.menu.MenuManager;
import controller.menu.Menu;
import model.*;
import model.cards.Card;
import model.cards.Hero;
import model.game.Game;
import model.game.GraveYard;
import model.items.Item;
import model.items.UsableItem;
import view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private View view;
    private Account currentAccount;
    private MenuManager menuManager;
    private Game game;

    public Controller() {
        view = new View();
        menuManager = new MenuManager(this);
        do {
            runCommand(view.getInputAsString());
        } while (menuManager.getCurrentMenu() != null);
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }

    public void runCommand(String command) {
        Menu currentMenu = menuManager.getCurrentMenu();
        try {
            Menu nextMenu = currentMenu.runCommandAndGetNextMenu(command);
            menuManager.setCurrentMenu(nextMenu);
        } catch (GameException e) {
            showMessage(e.getMessage());
        }
    }


    public void createAccount(String userName) {
        if (Utils.hasAccount(userName)) {
            throw new GameException("There is an account with this user name!");
        } else {
            Account account = new Account();
            account.setUserName(userName);
            showMessage("Enter Password:");
            account.setPassword(view.getInputAsString());
            Utils.add(account);
            showMessage("Account Created");
        }
    }

    public void login(String userName) {
        if (!Utils.hasAccount(userName)) {
            throw new GameException("There is no such account");
        }
        Account account = Utils.getAccountByUsername(userName);
        showMessage("Enter the password:");
        String password = getInputAsString();
        if (account.getPassword().equals(password)) {
            setCurrentAccount(account);
            showMessage("You've logged in successfully\n");
        } else {
            throw new GameException("Entered password is not correct!");
        }

//            todo remove test initializer
        account.getCollection().add(copyWithNewId(Utils.getShop().getHeroes().get(0)));
        List<Card> cards = Utils.getShop().getCards().stream().filter(card -> card.getClass() != Hero.class)
                .collect(Collectors.toList());
        for (int i = 0; i < 20; i++) {
            account.getCollection().add(copyWithNewId(cards.get(i)));
        }
        createDeck("amin");
        for (Card card : account.getCollection().getCards()) {
            addToDeck(card.getId(), "amin");
        }
        setMainDeck("amin");
//
    }

    public void showLeaderBoard() {
        StringBuilder leaderBoard = new StringBuilder();
        if (Utils.getAccounts().isEmpty()) {
            throw new GameException("There is no account!");
        }
        List<Account> sortedAccounts = Utils.getSortedAccounts();
        for (int i = 0; i < sortedAccounts.size(); i++) {
            Account account = sortedAccounts.get(i);
            leaderBoard.append(i + 1).append("-").append(account.getInfo()).append("\n");
        }
        showMessage(leaderBoard.toString());
    }

    public void save() {
    }

    public void logout() {
        if (currentAccount == null) {
            throw new GameException("You are not logged in!");
        } else {
            setCurrentAccount(null);
            showMessage("You've logged out successfully\n");
        }
    }

    public void showCollection() {
        showMessage(currentAccount.getCollection().getInfo());
    }

    public void searchInShop(String name) {
        Object object = Utils.getShop().getObjectByName(name);
        showMessage("This item/card is available in shop and it's id is:\n");
        if (object instanceof Card) {
            if (object.getClass() == Hero.class) {
                showMessage(Utils.getShop().getHeroId(((Hero) object).getName()));
            }
            showMessage(Utils.getShop().getCardId(((Card) object).getName()));
        } else if (object instanceof UsableItem) {
            showMessage(Utils.getShop().getItemId(((UsableItem) object).getName()));
        }
    }

    public void searchInCollection(String name) {
        String ids = currentAccount.getCollection().getIdsByName(name);
        showMessage("This item/card is available in collection and it's id is:\n");
        showMessage(ids);
    }

    public void buy(String name) {
        Object object = Utils.getShop().getObjectByName(name);
        if (object instanceof Card) {
            buyCard((Card) object);
        } else if (object instanceof UsableItem) {
            buyUsableItem((UsableItem) object);
        }
    }

    public void sell(String id) {
        Object object = currentAccount.getCollection().getObjectById(id);
        if (object instanceof Card) {
            sell((Card) object);
        } else if (object instanceof UsableItem) {
            sell((UsableItem) object);
        }
        showMessage("Sell was successful");
    }

    public void showShop() {
        showMessage(Utils.getShop().getInfo());
    }

    public void createDeck(String name) {
        if (currentAccount.hasDeck(name)) {
            throw new GameException("You have a deck with this name!");
        } else {
            currentAccount.createDeck(name);
        }
        showMessage("Deck created successfully");
    }

    public void deleteDeck(String name) {
        if (currentAccount.hasDeck(name)) {
            currentAccount.removeDeck(name);
            showMessage("Deck deleted successfully");
        } else {
            throw new GameException("You don't have a deck with this name!");
        }
    }

    public void addToDeck(String id, String name) {
        Object object = currentAccount.getCollection().getObjectById(id);
        Deck deck = currentAccount.getDeck(name);
        if (object instanceof Card) {
            deck.add((Card) object);
        } else if (object instanceof UsableItem) {
            deck.add((UsableItem) object);
        }
    }

    public void removeFromDeck(String id, String name) {
        Deck deck = currentAccount.getDeck(name);
        Object object = deck.getObjectById(id);
        if (object instanceof Card) {
            deck.remove((Card) object);
        } else if (object instanceof UsableItem) {
            deck.remove((UsableItem) object);
        }
    }

    public void validateDeck(String name) {
        Deck deck = currentAccount.getDeck(name);
        if (deck.isValid()) {
            showMessage("Deck is valid");
        } else {
            showMessage("Deck is NOT valid");
        }
    }

    public void validateMainDeck() {
        if (getMainDeck() == null) {
            throw new GameException("no deck is selected");
        }
        if (!getMainDeck().isValid()) {
            throw new GameException("selected deck is not valid");
        }
    }

    public void setMainDeck(String name) {
        currentAccount.setMainDeck(currentAccount.getDeck(name));
        showMessage("Deck" + name + "selected successfully");
    }

    private Deck getMainDeck() {
        return currentAccount.getMainDeck();
    }

    public void createGame(BattleMenu.StoryLevel storyLevel, int flagNumber) throws CloneNotSupportedException {

        BattleMenu.CustomGameMode mode = BattleMenu.CustomGameMode.getMode(storyLevel.value);
        Deck deck = createDeck(Integer.valueOf(storyLevel.value));
        game = Game.createGame(currentAccount, null, mode, deck, flagNumber);
    }

    public void createGame(int deckNumber, BattleMenu.CustomGameMode mode, int flagNumber) throws CloneNotSupportedException {
        Deck deck = createDeck(deckNumber);
        game = Game.createGame(currentAccount, null, mode, deck, flagNumber);
    }

    private Deck createDeck(int deckNumber) throws CloneNotSupportedException {

        List<Card> cards = new ArrayList<>();
        Item item = null;

        switch (deckNumber) {
            case 1:
                addCard(cards, "white-beast", "persian-archer", "transoxianain-spear-man",
                        "transoxianain-trench-raider", "transoxianain-trench-raider", "black-beast", "one-eyed-giant"
                        , "poisonous-snake", "gigantic-snake", "White-wolf", "high-witch", "nane-sarma", "siavash",
                        "beast-arjang", "total-disarm", "lightning-bolt", "all-disarm", "all-poison", "dispel",
                        "sacrifice", "kings-guard");
                item = getItem("wisdom-throne");
                break;
            case 2:
                addCard(cards, "zahhak", "persian-swordsman", "persian-spear-man", "persian-hero",
                        "transoxianain-sling-man", "transoxianain-prince", "giant-rock-thrower", "giant-rock-thrower",
                        "fire-dragon", "panther", "ghost", "giv", "iraj", "giant-king", "aria-dispel", "empower",
                        "god-strength", "poison-lake", "madness", "health-with-benefit", "kings-guard");
                item = getItem("soul-eater");
                break;
            case 3:
                addCard(cards, "arash", "persian-commander", "transoxianain-archer", "transoxianain-spy",
                        "giant-rock-thrower", "cavalry-beast", "cavalry-beast", "fierce-lion", "wolf", "witch", "wild" +
                                "-pig",
                        "piran", "bahman", "big-giant", "hellfire", "all-disarm", "dispel", "power-up", "all-power",
                        "all-attack", "weakening");
                item = getItem("terror-hood");
                break;
        }

        Deck deck = new Deck("AI_deck" + deckNumber);
        for (Card card : cards) {
            deck.add(card);
        }
        deck.add(item);
        return deck;
    }

    private void addCard(List<Card> cards, String... names) throws CloneNotSupportedException {
        for (String name : names) {
            Card card = ((Card) Utils.getShop().getObjectByName(name)).clone();
            String id = generateId(cards, card);
            card.setId(id);
            cards.add(card);
        }
    }

    private Item getItem(String name) throws CloneNotSupportedException {
        Item item = ((Item) Utils.getShop().getObjectByName(name)).clone();
        item.setId("AI_Player_" + name + "_1");
        return item;
    }

    private String generateId(List<Card> cards, Card card) {
        int index = 1;
        index += cards.stream().filter(card1 -> card.nameEquals(card1.getName())).count();
        return "AI_Player_" + card.getName() + "_" + index;
    }

    public void showGameInfo(){
        //Game should have a method to return an info
    }

    public void showAllyMinions(){
        //get all minions of currentPlayer in game
    }

    public void showEnemyMinions(){
        //get all minions of other player than currentPlayer in game
    }

    public void selectCard(String id){
        //Game have selectedCell, this cell has a card.
        //set the selectedCell by this cardId
    }

    public void showCardInfo(String id){
        //show the info of the card of selectedCell
    }

    public void showHand(){}

    public void insertCardInXY(String id, int x, int y){ }

    public void endTurn(){ }

    public void showCollectibles(){}

    public void selectCollectible(String itemId){
        //search in the collectableItems owned by currentPlayer and get it
        //Game should have a collectableItem, set this
    }

    public void showNextCard(){}

    public GraveYard getGraveYard(){
        return game.getCurrentPlayer().getGraveYard();
    }

    public void showCollectableItemInfo(){ }

    public void useCollectibleItem(int x, int y){ }

    public void moveSelectedCardTo(int x, int y){}

    public void attackSelectedCardOn(String opponentId){}

    public void useSpecialPowerOfSelectedCardOn(int x, int y){}

    public void attackCombo(String opponentId, String[] allyCardIds){}

    public List<Card> getMovableCards(){
        return game.getCurrentPlayer().getHand().stream()
                .map(card -> (Hero) card)
                .filter(hero -> hero.getInGame().isArmed())
                .collect(Collectors.toList());
    }

    public List<Card> getAttackingCards(){
        //return the cards that can be attacked by ally hero and minions
        return null;
    }

    public List<Card> getInsertableCards(){ return null; }

    public void showAllDecksInfo() {
        showMessage(currentAccount.showAllDecks());
    }

    public void showDeckInfo(String name) {
        showMessage(currentAccount.getDeck(name).getInfo());
    }

    private String generateNewId(Card newCard) {
        int index = 1;
        index += currentAccount.getCollection().getCards().stream()
                .filter(card -> card.nameEquals(newCard.getName()))
                .count();
        return currentAccount.getUserName() + "_" + newCard.getName() + "_" + index;
    }

    private String generateNewId(Item newItem) {
        int index = 1;
        index += currentAccount.getCollection().getUsableItems().stream()
                .filter(card -> card.nameEquals(newItem.getName()))
                .count();
        return currentAccount.getUserName() + "_" + newItem.getName() + "_" + index;
    }

    private Card copyWithNewId(Card card) {
        String id = generateNewId(card);
        try {
            Card copiedCard = card.clone();
            copiedCard.setId(id);
            copiedCard.setAccountName(currentAccount.getUserName());
            return copiedCard;
        } catch (CloneNotSupportedException c) {
        }
        return null;
    }

    private Item copyWithNewId(UsableItem usableItem) {
        String id = generateNewId(usableItem);
        try {
            UsableItem copiedItem = usableItem.clone();
            copiedItem.setId(id);
            copiedItem.setAccountName(currentAccount.getUserName());
            return copiedItem;
        } catch (CloneNotSupportedException c) {
        }
        return null;
    }

    private void buyCard(Card card) {
        currentAccount.decreaseBudget(card.getPrice());
        currentAccount.getCollection().add(copyWithNewId(card));
        showMessage("You have bought the card successfully!");
    }

    private void buyUsableItem(UsableItem usableItem) {
        currentAccount.decreaseBudget(usableItem.getPrice());
        currentAccount.getCollection().add(copyWithNewId(usableItem));
        showMessage("You have bought the item successfully!");
    }

    private void sell(Card card) {
        currentAccount.getCollection().remove(card);
        currentAccount.increaseBudget(card.getPrice());
    }

    private void sell(UsableItem usableItem) {
        currentAccount.getCollection().remove(usableItem);
        currentAccount.increaseBudget(usableItem.getPrice());
    }

    public void showMessage(String message) {
        view.show(message);
    }

    public String getInputAsString() {
        return view.getInputAsString();
    }
}
