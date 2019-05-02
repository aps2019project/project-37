package controller;

import controller.menu.MenuManager;
import controller.menu.Menu;
import model.*;
import model.game.Game;
import view.View;

public class Controller {
    private View view;
    private Account currentAccount;
    private AllAccounts allAccounts;
    private MenuManager menuManager;
    private Game game;

    public Controller(){
        view = new View();
        allAccounts = new AllAccounts();
        menuManager = new MenuManager(this);
    }
    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }
    public void runCommand(String command){
        Menu currentMenu = menuManager.getCurrentMenu();
        Menu nextMenu =  currentMenu.runCommandAndGetNextMenu(command);
        menuManager.setCurrentMenu(nextMenu);
    }
    public void createAccount(String userName){
        if(allAccounts.hasAccount(userName)){
            throw new GameException("There is an account with this user name!");
        }
        else{
            Account account = new Account();
            account.setUserName(userName);
            view.show("Enter Password:");
            account.setPassword(view.getInputAsString());
        }
    }
    public void login(String userName){
        if(currentAccount == null){
            throw new GameException("You should log out!");
        }
        Account account = allAccounts.getAccount(userName);
        showMessage("Enter the password:");
        String password = getInputAsString();
        if(account.getPassword().equals(password)){
            setCurrentAccount(account);
        }
        else {
            throw new GameException("Entered password is not correct!");
        }
    }
    public void showLeaderBoard(){
        StringBuilder leaderBoard = new StringBuilder();
        int count = 1;
        if(allAccounts.getAccounts().isEmpty()){
            throw new GameException("There is no account!");
        }
        for(Account account:allAccounts.getAccounts()){
            leaderBoard.append(count + "-" + account.getInfo()+"\n");
            count ++;
        }
    }
    public void save(){}
    public void logOut(){
        if(currentAccount == null){
            throw new GameException("You are not logged in!");
        }
        else{
            setCurrentAccount(null);
        }
    }
    public void showCollection(){
        showMessage(currentAccount.getCollection().getInfo());
    }
    public void searchInShop(String name){
        Object object = allAccounts.getShop().getObjectByName(name);
        if(object instanceof Card){
            showMessage(generateNewId((Card) object));
        }else if(object instanceof  UsableItem){
            showMessage(generateNewId((UsableItem) object));
        }
    }
    public void searchInCollection(String name) throws GameException {
        showMessage(currentAccount.getCollection().getIdsByName(name));
    }
    public void buy(String name){
        Object GameObject = allAccounts.getShop().getObjectByName(name);
        if(GameObject instanceof Card){
            buyCard((Card) GameObject);
        }else if(GameObject instanceof UsableItem){
            buyUsableItem((UsableItem) GameObject);
        }
    }
    public void sell(String id){
        Object object = currentAccount.getCollection().getObjectById(id);
        if(object instanceof Card){
            sell((Card) object);
        }else if(object instanceof UsableItem){
            sell((UsableItem) object);
        }
    }
    public void showShop(){
        showMessage(allAccounts.getShop().getInfo());
    }
    public void createDeck(String name){
        if(currentAccount.hasDeck(name)){
            throw new GameException("You has a deck with this name!");
        }else{
            currentAccount.createDeck(name);
        }
    }
    public void deleteDeck(String name){
        if(currentAccount.hasDeck(name)){
            currentAccount.removeDeck(name);
        }else{
            throw new GameException("You don't have a deck with this name!");
        }
    }
    public void addToDeck(String id,String name){
        Object object = currentAccount.getCollection().getObjectById(id);
        Deck deck = currentAccount.getDeck(name);
        if(object instanceof Card){
            deck.add((Card) object);
        }else if(object instanceof UsableItem){
            deck.add((UsableItem) object);
        }
    }
    public void removeFromDeck(String id, String name){
        Deck deck = currentAccount.getDeck(name);
        Object object = deck.getObjectById(id);
        if(object instanceof Card){
            deck.remove((Card) object);
        }else if(object instanceof UsableItem){
            deck.remove((UsableItem) object);
        }
    }
    public void validateDeck(String name){
        Deck deck = currentAccount.getDeck(name);
        if(deck.isValidated()){
            showMessage("Deck is validated");
        }else {
            showMessage("Deck is NOT validated");
        }
    }
    public void setMainDeck(String name){
        currentAccount.setMainDeck(currentAccount.getDeck(name));
    }
    public void showAllDecksInfo(){
        showMessage(currentAccount.getMainDeck().getInfo());
        currentAccount.getDecks().stream()
                .filter(deck -> !deck.equals(currentAccount.getMainDeck()))
                .forEach(deck -> showMessage(deck.getInfo()));
    }
    public void showDeckInfo(String name){
        showMessage(currentAccount.getDeck(name).getInfo());
    }

    private String generateNewId(Card newCard){
        int index = 1;
        index += currentAccount.getCollection().getCards().stream()
                .filter(card -> card.nameEquals(newCard.getName()))
                .count();
        return currentAccount.getUserName() + "_" + newCard.getName() +"_" + index;
    }
    private String generateNewId(Item newItem){
        int index = 1;
        index += currentAccount.getCollection().getUsableItems().stream()
                .filter(card -> card.nameEquals(newItem.getName()))
                .count();
        return currentAccount.getUserName() + "_" + newItem.getName() +"_" + index;
    }
    private Card copyWithNewId(Card card){
        String id= generateNewId(card);
        try {
            Card copiedCard = card.clone();
            copiedCard.setId(id);
            return copiedCard;
        }catch (CloneNotSupportedException c){
        }
        return null;
    }
    private Item copyWithNewId(UsableItem usableItem){
        String id= generateNewId(usableItem);
        try {
            UsableItem copiedItem = usableItem.clone();
            copiedItem.setId(id);
            return copiedItem;
        }catch (CloneNotSupportedException c){
        }
        return null;
    }
    private void buyCard(Card card){
        currentAccount.decreaseBudget(card.getPrice());
        currentAccount.getCollection().add(copyWithNewId(card));
        showMessage("You have bought the card successfully!");
    }
    private void buyUsableItem(UsableItem usableItem){
        currentAccount.decreaseBudget(usableItem.getPrice());
        currentAccount.getCollection().add(copyWithNewId(usableItem));
        showMessage("You have bought the item successfully!");
    }
    private void sell(Card card){
        currentAccount.getCollection().remove(card);
        currentAccount.increaseBudget(card.getPrice());
    }
    private void sell(UsableItem usableItem){
        currentAccount.getCollection().remove(usableItem);
        currentAccount.increaseBudget(usableItem.getPrice());
    }
    public void showMessage(String message){
        view.show(message);
    }
    public String getInputAsString(){
        return view.getInputAsString();
    }
}
