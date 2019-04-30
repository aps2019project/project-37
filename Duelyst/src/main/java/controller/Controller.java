package controller;

import controller.menu.MenuManager;
import controller.menu.Menu;
import model.*;
import view.View;

public class Controller {
    private View view;
    private Account currentAccount;
    private AllAccounts allAccounts;
    private MenuManager menuManager;

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
        Shop shop = allAccounts.getShop();
        if(shop.hasByName(name)){
            if(shop.hasCardByName(name)){
                Card card = shop.getCardByName(name);
                String id = generateNewId(card);
                showMessage(id);
            }
            if(shop.hasUsableItemByName(name)){
                Item item = shop.getUsableItemByName(name);
                String id = generateNewId(item);
                showMessage(id);
            }
        }else {
            throw new GameException("No card or item with this name!");
        }
    }
    public void searchInCollection(String name){
        Collection collection = currentAccount.getCollection();
        if(collection.hasByName(name)){
            showMessage(collection.getIdsByName(name));
        }
        else{
            throw new GameException("No item or card with this name!");
        }
    }
    public void buy(String name){
        Shop shop = allAccounts.getShop();
        if(shop.hasByName(name)) {
            if (shop.hasCardByName(name)) {
                buyCard(shop.getCardByName(name));
            }
            if (shop.hasUsableItemByName(name)) {
                buyUsableItem(shop.getUsableItemByName(name));
            }
        }else {
            throw new GameException("No card or item with this name");
        }
    }
    public void sell(String id){
        Collection collection = currentAccount.getCollection();
        if(collection.hasById(id)){
            if (collection.hasCardById(id)) {
                sell(collection.getCardById(id));
            }
            if (collection.hasUsableItemById(id)) {
                sell(collection.getUsableItemById(id));
            }
        }else {
            throw new GameException("You don't have any card or item this id!");
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
    private String generateNewId(Card newCard){
        int count = 1;
        for(Card card:currentAccount.getCollection().getCards()){
            if(card.nameEquals(newCard.getName())){
                count ++;
            }
        }
        return currentAccount.getUserName() + "_" + newCard.getName() +"_" + count;
    }
    private String generateNewId(Item newItem){
        int count = 1;
        for(Item item:currentAccount.getCollection().getItems()){
            if(item.nameEquals(newItem.getName())){
                count ++;
            }
        }
        return currentAccount.getUserName() + "_" + newItem.getName() +"_" + count;
    }
    private Card copyWithNewId(Card card){
        String id= generateNewId(card);
        if(card instanceof Spell){
            Spell spell = (Spell) card;
            return new Spell(id,spell);
        }
        if(card instanceof Minion){
            Minion minion = (Minion) card;
            return new Minion(id,minion);
        }
        if(card instanceof Hero){
            Hero hero = (Hero) card;
            return new Hero(id,hero);
        }
        return null;
    }
    private Item copyWithNewId(UsableItem usableItem){
        String id = generateNewId(usableItem);
        return new UsableItem(id,usableItem);
    }
    private void buyCard(Card card){
        if (card.getPrice() <= currentAccount.getBudget()) {
            currentAccount.decreaseBudget(card.getPrice());
            currentAccount.getCollection().add(copyWithNewId(card));
            showMessage("You have bought the card successfully!");
        }else {
            throw new GameException("Budget is not enough!");
        }
    }
    private void buyUsableItem(UsableItem usableItem){
        if(currentAccount.getCollection().getUsableItems().size() < 3) {
            if (usableItem.getPrice() <= currentAccount.getBudget()) {
                currentAccount.decreaseBudget(usableItem.getPrice());
                currentAccount.getCollection().add(copyWithNewId(usableItem));
                showMessage("You have bought the item successfully!");
            }
            else {
                throw new GameException("Budget is not enough!");
            }
        }else {
            throw new GameException("You cannot have anymore items!");
        }
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
