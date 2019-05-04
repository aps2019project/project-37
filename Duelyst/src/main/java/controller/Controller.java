package controller;

import controller.menu.MenuManager;
import controller.menu.Menu;
import model.*;
import model.game.Game;
import view.View;

import java.util.List;

public class Controller {
    private View view;
    private Account currentAccount;
    private MenuManager menuManager;
    private Game game;

    public Controller() {
        view = new View();
        menuManager = new MenuManager(this);
        while (true) {
            runCommand(view.getInputAsString());
            if (menuManager.getCurrentMenu() == null) {
                break;
            }
        }
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
            if (object instanceof Hero) {
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

    public void setMainDeck(String name) {
        currentAccount.setMainDeck(currentAccount.getDeck(name));
        showMessage("Deck" + name + "selected successfully");
    }

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
            copiedCard.setAccount(currentAccount);
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
            copiedItem.setAccount(currentAccount);
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
